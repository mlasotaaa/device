package org.example.device.application;

import org.example.device.application.command.CreateDeviceCommand;
import org.example.device.application.command.UpdateDeviceCommand;
import org.example.device.domain.exception.DeviceInUseException;
import org.example.device.domain.exception.DeviceNotFoundException;
import org.example.device.domain.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeviceServiceTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");
    static {
        postgres.start();
    }

    @AfterEach
    void cleanAfterEachTest() {
        jdbcTemplate.execute("TRUNCATE TABLE devices CASCADE");
    }

    @Test
    void shouldCreateAndSaveDevice() {
        // given
        CreateDeviceCommand command = new CreateDeviceCommand(
                new DeviceName("Iphone 17"),
                new DeviceBrand("Apple"),
                DeviceState.AVAILABLE
        );

        // when
        Device device = deviceService.createDevice(command);

        // then
        assertEquals(new DeviceName("Iphone 17"), device.deviceName());
        assertEquals(new DeviceBrand("Apple"), device.deviceBrand());
        assertEquals(DeviceState.AVAILABLE, device.deviceState());
    }

    @Test
    void shouldCreateAndUpdateSavedDevice() {
        // given
        CreateDeviceCommand command = new CreateDeviceCommand(
                new DeviceName("Iphone 17"),
                new DeviceBrand("Apple"),
                DeviceState.AVAILABLE
        );

        Device device = deviceService.createDevice(command);

        // when
        Device updatedDevice = deviceService.updateDevice(
                device.deviceId(),
                new UpdateDeviceCommand(new DeviceName("Updated Device name"), null, null)
        );

        // then
        assertEquals(new DeviceName("Updated Device name"), updatedDevice.deviceName());
        assertEquals(new DeviceBrand("Apple"), updatedDevice.deviceBrand());
        assertEquals(DeviceState.AVAILABLE, updatedDevice.deviceState());
    }

    @Test
    void shouldCreateOnceAndUpdateSavedDevice() {
        // given
        CreateDeviceCommand command = new CreateDeviceCommand(
                new DeviceName("Iphone 17"),
                new DeviceBrand("Apple"),
                DeviceState.AVAILABLE
        );

        Device device = deviceService.createDevice(command);

        // when
        deviceService.updateDevice(
                device.deviceId(),
                new UpdateDeviceCommand(new DeviceName("Updated Device name"), null, null)
        );

        Device updatedDeviceSecondTime = deviceService.updateDevice(
                device.deviceId(),
                new UpdateDeviceCommand(new DeviceName("Second time updated Device name"), null, null)
        );

        // then
        assertEquals(new DeviceName("Second time updated Device name"), updatedDeviceSecondTime.deviceName());
        assertEquals(new DeviceBrand("Apple"), updatedDeviceSecondTime.deviceBrand());
        assertEquals(DeviceState.AVAILABLE, updatedDeviceSecondTime.deviceState());

        assertThat(List.of(updatedDeviceSecondTime))
                .usingRecursiveComparison()
                .isEqualTo(deviceService.getAllDevices());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingInUseDevice() {
        // given
        Device inUseDevice = deviceService.createDevice(new CreateDeviceCommand(
                new DeviceName("Name"),
                new DeviceBrand("Brand"),
                DeviceState.IN_USE
        ));

        UpdateDeviceCommand updateCommand = new UpdateDeviceCommand(
                new DeviceName("New Name"),
                null,
                null
        );

        // when & then
        DeviceInUseException exception = assertThrows(DeviceInUseException.class, () ->
                deviceService.updateDevice(inUseDevice.deviceId(), updateCommand)
        );

        String expectedMessage = String.format("Cannot update device %s because it is currently IN_USE.", inUseDevice.deviceId().value());
        assertEquals(expectedMessage, exception.getMessage());

        Device found = deviceService.getDevice(inUseDevice.deviceId());

        assertThat(inUseDevice)
                .usingRecursiveComparison()
                .isEqualTo(found);
    }

    @Test
    void shouldGetDeviceById() {
        // given
        Device created = deviceService.createDevice(new CreateDeviceCommand(
                new DeviceName("MacBook Pro"), new DeviceBrand("Apple"), DeviceState.AVAILABLE));

        // when
        Device found = deviceService.getDevice(created.deviceId());

        // then
        assertEquals(new DeviceName("MacBook Pro"), found.deviceName());

        assertThat(created)
                .usingRecursiveComparison()
                .isEqualTo(found);
    }

    @Test
    void shouldGetDevicesByState() {
        // given
        Device deviceOne = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("D1"),
                        new DeviceBrand("B1"),
                        DeviceState.AVAILABLE
                )
        );
        Device deviceTwo = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("D2"),
                        new DeviceBrand("B2"),
                        DeviceState.IN_USE
                )
        );
        Device deviceThree = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("D3"),
                        new DeviceBrand("B3"),
                        DeviceState.AVAILABLE
                )
        );

        // when
        List<Device> availableDevices = deviceService.getDevicesByState(DeviceState.AVAILABLE);

        // then
        assertThat(availableDevices)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(deviceOne, deviceThree));
    }

    @Test
    void shouldGetDevicesByBrand() {
        // given
        DeviceBrand apple = new DeviceBrand("Apple");
        DeviceBrand samsung = new DeviceBrand("Samsung");

        Device deviceOne = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("iPhone"),
                        apple,
                        DeviceState.AVAILABLE
                )
        );
        Device deviceTwo = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("iPad"),
                        apple,
                        DeviceState.AVAILABLE
                )
        );
        Device deviceThree = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("Galaxy"),
                        samsung,
                        DeviceState.AVAILABLE
                )
        );

        // when
        List<Device> appleDevices = deviceService.getDevicesByBrand(apple);

        // then
        assertThat(appleDevices)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(deviceOne, deviceTwo));
    }

    @Test
    void shouldGetAllDevices() {
        // given
        Device deviceOne = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("Device 1"),
                        new DeviceBrand("Brand A"),
                        DeviceState.AVAILABLE
                )
        );

        Device deviceTwo = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("Device 2"),
                        new DeviceBrand("Brand B"),
                        DeviceState.AVAILABLE
                )
        );

        // when
        List<Device> allDevices = deviceService.getAllDevices();

        // then
        assertThat(allDevices)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(deviceOne, deviceTwo));
    }

    @Test
    void shouldDeleteDevice() {
        // given
        Device device = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("To Delete"),
                        new DeviceBrand("Brand"),
                        DeviceState.AVAILABLE
                )
        );
        DeviceId id = device.deviceId();

        // when
        deviceService.deleteDevice(id);

        // then
        DeviceNotFoundException exception = assertThrows(DeviceNotFoundException.class,
                () -> deviceService.getDevice(id));

        String expectedMessage = String.format("Device with ID %s not found.", device.deviceId().value());
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingInUseDevice() {
        // given
        Device device = deviceService.createDevice(
                new CreateDeviceCommand(
                        new DeviceName("Active Phone"),
                        new DeviceBrand("Apple"),
                        DeviceState.IN_USE
                )
        );

        // when & then
        DeviceInUseException exception = assertThrows(DeviceInUseException.class,
                () -> deviceService.deleteDevice(device.deviceId()));

        String expectedMessage = String.format("Device %s is IN_USE and can not be deleted.", device.deviceId().value());
        assertEquals(expectedMessage, exception.getMessage());

        Device found = deviceService.getDevice(device.deviceId());

        assertThat(device)
                .usingRecursiveComparison()
                .isEqualTo(found);
    }
}