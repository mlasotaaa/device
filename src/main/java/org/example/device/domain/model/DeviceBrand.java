package org.example.device.domain.model;

import org.example.device.domain.exception.DomainAssertion;

public record DeviceBrand(String value) {

    public DeviceBrand {
        DomainAssertion.assertNotEmpty(value, "value");
    }
}
