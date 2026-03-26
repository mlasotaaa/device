package org.example.device.application.service;

import org.example.device.application.DeviceRepository;
import org.example.device.application.DeviceService;
import org.example.device.application.command.CreateDeviceCommand;
import org.example.device.application.command.UpdateDeviceCommand;
import org.example.device.domain.exception.DeviceNotFoundException;
import org.example.device.domain.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final ConcurrentHashMap<DeviceId, ReentrantLock> deviceLocks = new ConcurrentHashMap<>();

    @Autowired
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

    /**
     * Updates the device data using a local locking mechanism.
     * * NOTE: The current implementation uses ReentrantLock with a ConcurrentHashMap.
     * This ensures thread-safety and data consistency ONLY within a SINGLE JVM instance.
     * This lock prevents race conditions for parallel requests processed by the same process.
     * * Recommendations for horizontal scaling:
     * When running multiple application replicas (e.g., multi-instance/Kubernetes),
     * a local lock will no longer be sufficient. Consider the following:
     * 1. Optimistic Locking (@Version) + Retry: Highly performant, database-driven approach.
     * 2. Distributed Locking (e.g., Redis/Redlock, ShedLock): If strict synchronization
     * across instances is required.
     * 3. Database Pessimistic Locking (SELECT FOR UPDATE): If operations are short-lived
     * and you want to avoid additional infrastructure overhead.
     */
    @Override
    public Device updateDevice(DeviceId deviceId, UpdateDeviceCommand command) {
        ReentrantLock lock = deviceLocks.computeIfAbsent(deviceId, k -> new ReentrantLock());
        lock.lock();
        try {
            Device device = getDevice(deviceId);

            Device updatedDevice = device.update(
                    command.name(),
                    command.brand(),
                    command.state()
            );

            return deviceRepository.save(updatedDevice);
        } finally {
            lock.unlock();
        }
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
