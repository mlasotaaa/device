package org.example.device.domain.exception;

import org.example.device.domain.model.DeviceId;

public class DeviceNotFoundException extends RuntimeException {

    private DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(DeviceId id) {
        super(String.format("Device with ID %s not found.", id.value()));
    }
}