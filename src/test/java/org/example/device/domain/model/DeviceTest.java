package org.example.device.domain.model;

import org.example.device.domain.exception.DeviceInUseException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeviceTest {

    @Test
    void shouldUpdateOnlyBrand() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.AVAILABLE);

        // when
        Device updated = device.update(null, new DeviceBrand("New Brand"), null);

        // then
        assertEquals(new DeviceBrand("New Brand"), updated.deviceBrand());
        assertEquals(device.deviceName(), updated.deviceName());
        assertEquals(device.deviceState(), updated.deviceState());
        assertEquals(device.createdAt(), updated.createdAt());
    }

    @Test
    void shouldUpdateOnlyName() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.AVAILABLE);

        // when
        Device updated = device.update(new DeviceName("New name"), null, null);

        // then
        assertEquals(new DeviceName("New name"), updated.deviceName());
        assertEquals(device.deviceBrand(), updated.deviceBrand());
        assertEquals(device.deviceState(), updated.deviceState());
        assertEquals(device.createdAt(), updated.createdAt());
    }

    @Test
    void shouldUpdateOnlyState() {
        // given
        Device device = Device.create(new DeviceName("OldName"), new DeviceBrand("OldBrand"), DeviceState.AVAILABLE);

        // when
        Device updated = device.update(null, null, DeviceState.IN_USE);

        // then
        assertEquals(new DeviceName("OldName"), updated.deviceName());
        assertEquals(new DeviceBrand("OldBrand"), updated.deviceBrand());
        assertEquals(DeviceState.IN_USE, updated.deviceState());
        assertEquals(device.createdAt(), updated.createdAt());
    }

    @Test
    void shouldUpdateOnlyStateInUseToInActive() {
        // given
        Device device = Device.create(new DeviceName("OldName"), new DeviceBrand("OldBrand"), DeviceState.IN_USE);

        // when
        Device updated = device.update(null, null, DeviceState.INACTIVE);

        // then
        assertEquals(new DeviceName("OldName"), updated.deviceName());
        assertEquals(new DeviceBrand("OldBrand"), updated.deviceBrand());
        assertEquals(DeviceState.INACTIVE, updated.deviceState());
        assertEquals(device.createdAt(), updated.createdAt());
    }

    @Test
    void shouldUpdateOnlyStateWhenOtherFieldsAreSame() {
        // given
        Device device = Device.create(new DeviceName("OldName"), new DeviceBrand("OldBrand"), DeviceState.AVAILABLE);

        // when
        Device updated = device.update(new DeviceName("OldName"), new DeviceBrand("OldBrand"), DeviceState.IN_USE);

        // then
        assertEquals(new DeviceName("OldName"), updated.deviceName());
        assertEquals(new DeviceBrand("OldBrand"), updated.deviceBrand());
        assertEquals(DeviceState.IN_USE, updated.deviceState());
        assertEquals(device.createdAt(), updated.createdAt());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNameOfDeviceInUse() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.IN_USE);

        // when & then
        assertThrows(DeviceInUseException.class, () ->
                device.update(new DeviceName("New Name"), null, null)
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingBrandOfDeviceInUse() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.IN_USE);

        // when & then
        assertThrows(DeviceInUseException.class, () ->
                device.update(null, new DeviceBrand("New brand"), null)
        );
    }

    @Test
    void shouldThrowExceptionWhenUpdatingBrandAndNameOfDeviceInUse() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.IN_USE);

        // when & then
        assertThrows(DeviceInUseException.class, () ->
                device.update(new DeviceName("New Name"), new DeviceBrand("New brand"), DeviceState.INACTIVE)
        );
    }

    @Test
    void shouldKeepEverythingWhenAllArgumentsAreNull() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.AVAILABLE);

        // when
        Device updated = device.update(null, null, null);

        // then
        assertEquals(device, updated);
    }

    @Test
    void shouldThrowExceptionWhenDeletingDeviceInUse() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.IN_USE);

        // when & then
        assertThrows(DeviceInUseException.class, device::validateCanBeDeleted);
    }

    @Test
    void shouldAllowDeletionWhenAvailable() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.AVAILABLE);

        // when & then
        assertDoesNotThrow(device::validateCanBeDeleted);
    }

    @Test
    void shouldAllowDeletionWhenInactive() {
        // given
        Device device = Device.create(new DeviceName("Name"), new DeviceBrand("Brand"), DeviceState.INACTIVE);

        // when & then
        assertDoesNotThrow(device::validateCanBeDeleted);
    }
}