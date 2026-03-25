package org.example.device.domain.exception;

import org.example.common.domain.DomainException;

public final class DomainAssertion {

    private DomainAssertion() {
    }

    public static void assertNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new DomainException(fieldName + " cannot be null");
        }
    }

    public static void assertNotEmpty(String value, String fieldName) {
        assertNotNull(value, fieldName);
        if (value.trim().isEmpty()) {
            throw new DomainException(fieldName + " cannot be empty");
        }
    }
}