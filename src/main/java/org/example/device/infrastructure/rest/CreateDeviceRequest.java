package org.example.device.infrastructure.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

record CreateDeviceRequest(
        @NotBlank(message = "brand cannot be empty") String brand,
        @NotBlank(message = "name cannot be empty") String name,
        @Pattern(
                regexp = "AVAILABLE|INACTIVE|IN_USE",
                message = "State must be AVAILABLE or INACTIVE or IN_USE"
        ) String status) {
}
