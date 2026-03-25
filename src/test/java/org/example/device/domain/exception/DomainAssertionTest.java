package org.example.device.domain.exception;

import org.example.common.domain.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DomainAssertionTest {

    @Nested
    @DisplayName("assertNotNull tests")
    class NotNullTests {

        @Test
        @DisplayName("Should throw DomainException when value is null")
        void shouldThrowExceptionWhenValueIsNull() {
            // given
            String fieldName = "deviceId";

            // when & then
            DomainException exception = assertThrows(DomainException.class,
                    () -> DomainAssertion.assertNotNull(null, fieldName));

            assertEquals("deviceId cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should not throw exception when value is present")
        void shouldNotThrowExceptionWhenValueIsPresent() {
            assertDoesNotThrow(() -> DomainAssertion.assertNotNull(new Object(), "field"));
        }
    }

    @Nested
    @DisplayName("assertNotEmpty tests")
    class NotEmptyTests {

        @Test
        @DisplayName("Should throw DomainException when string is null")
        void shouldThrowExceptionWhenStringIsNull() {
            DomainException exception = assertThrows(DomainException.class,
                    () -> DomainAssertion.assertNotEmpty(null, "name"));

            assertEquals("name cannot be null", exception.getMessage());
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   ", "\n", "\t"})
        @DisplayName("Should throw DomainException when string is empty or contains only whitespace")
        void shouldThrowExceptionWhenStringIsBlank(String blankValue) {
            // given
            String fieldName = "brand";

            // when & then
            DomainException exception = assertThrows(DomainException.class,
                    () -> DomainAssertion.assertNotEmpty(blankValue, fieldName));

            assertEquals("brand cannot be empty", exception.getMessage());
        }

        @Test
        @DisplayName("Should not throw exception when string contains text")
        void shouldNotThrowWhenStringIsValid() {
            assertDoesNotThrow(() -> DomainAssertion.assertNotEmpty("Valid Name", "name"));
        }
    }
}