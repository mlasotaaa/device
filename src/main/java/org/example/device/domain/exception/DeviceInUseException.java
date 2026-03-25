package org.example.device.domain.exception;

import org.example.device.domain.model.DeviceId;

public class DeviceInUseException extends RuntimeException {

    private DeviceInUseException(String message) {
        super(message);
    }

    public static DeviceInUseException forUpdate(DeviceId id) {
        return new DeviceInUseException(String.format("Cannot update device %s because it is currently IN_USE.", id.value()));
    }

    public static DeviceInUseException forDeletion(DeviceId id) {
        return new DeviceInUseException(String.format("Device %s is IN_USE and can not be deleted.", id.value()));
    }
}
