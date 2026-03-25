package org.example.device.application.service;

import org.example.device.application.DeviceRepository;
import org.example.device.application.DeviceService;
import org.example.device.application.command.CreateDeviceCommand;
import org.example.device.application.command.UpdateDeviceCommand;
import org.example.device.domain.exception.DeviceNotFoundException;
import org.example.device.domain.model.*;

import java.util.List;

public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceServiceImpl(final DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Device createDevice(CreateDeviceCommand command) {
        Device device = Device.create(
                command.deviceName(),
                command.deviceBrand(),
                command.deviceState()
        );
        return deviceRepository.save(device);
    }

    @Override
    public Device updateDevice(DeviceId deviceId, UpdateDeviceCommand command) {
        Device device = getDevice(deviceId);

        Device updatedDevice = device.update(
                command.name(),
                command.brand(),
                command.state()
        );

        return deviceRepository.save(updatedDevice);
    }

    @Override
    public Device getDevice(DeviceId deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException(deviceId));
    }

    @Override
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    @Override
    public List<Device> getDevicesByBrand(DeviceBrand brand) {
        return deviceRepository.findByBrand(brand);
    }

    @Override
    public List<Device> getDevicesByState(DeviceState deviceState) {
        return deviceRepository.findByState(deviceState);
    }

    @Override
    public void deleteDevice(DeviceId deviceId) {
        Device device = getDevice(deviceId);
        device.validateCanBeDeleted();
        deviceRepository.delete(deviceId);
    }
}
