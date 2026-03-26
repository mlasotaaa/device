package org.example.device.domain.model;

import org.example.common.domain.DomainException;

public enum DeviceState {
    AVAILABLE,
    IN_USE,
    INACTIVE;

    public static DeviceState fromString(String value) {
        try {
            return DeviceState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new DomainException("Invalid device state: " + value);
        }
    }
}
