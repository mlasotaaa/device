package org.example.device.domain.model;

import org.example.device.domain.exception.DeviceInUseException;
import org.example.device.domain.exception.DomainAssertion;

import java.time.Instant;
import java.util.Optional;

public record Device(
        DeviceId deviceId,
        DeviceName deviceName,
        DeviceBrand deviceBrand,
        DeviceState deviceState,
        Instant createdAt
) {
    public Device {
        DomainAssertion.assertNotNull(deviceId, "deviceId");
        DomainAssertion.assertNotNull(deviceName, "deviceName");
        DomainAssertion.assertNotNull(deviceBrand, "deviceBrand");
        DomainAssertion.assertNotNull(deviceState, "deviceState");
        DomainAssertion.assertNotNull(createdAt, "createdAt");
    }

    public static Device create(DeviceName deviceName, DeviceBrand deviceBrand, DeviceState deviceState) {
        return new Device(DeviceId.of(), deviceName, deviceBrand, deviceState, Instant.now());
    }

    public Device update(DeviceName newName, DeviceBrand newBrand, DeviceState newState) {
        if (this.deviceState == DeviceState.IN_USE) {
            boolean nameChanged = newName != null && !this.deviceName.equals(newName);
            boolean brandChanged = newBrand != null && !this.deviceBrand.equals(newBrand);

            if (nameChanged || brandChanged) {
                throw DeviceInUseException.forUpdate(deviceId);
            }
        }

        return new Device(
                this.deviceId,
                Optional.ofNullable(newName).orElse(this.deviceName),
                Optional.ofNullable(newBrand).orElse(this.deviceBrand),
                Optional.ofNullable(newState).orElse(this.deviceState),
                this.createdAt
        );
    }

    public void validateCanBeDeleted() {
        if (this.deviceState() == DeviceState.IN_USE) {
            throw DeviceInUseException.forDeletion(deviceId);
        }
    }
}