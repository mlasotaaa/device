package org.example.device.domain.model;

import org.example.device.domain.exception.DomainAssertion;

public record DeviceName(String value) {

    public DeviceName {
        DomainAssertion.assertNotEmpty(value, "value");
    }
}
