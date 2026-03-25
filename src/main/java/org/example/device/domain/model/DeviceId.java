package org.example.device.domain.model;

import org.example.device.domain.exception.DomainAssertion;

import java.util.UUID;

public record DeviceId(UUID value) {

    public DeviceId {
        DomainAssertion.assertNotNull(value, "value");
    }

    static DeviceId of() {
        return new DeviceId(UUID.randomUUID());
    }
}
