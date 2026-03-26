package org.example.device.infrastructure.rest;

import jakarta.validation.constraints.Pattern;

record UpdatePartiallyDeviceRequest(String brand,
                                    String name,
                                    @Pattern(
                                            regexp = "AVAILABLE|INACTIVE|IN_USE",
                                            message = "State must be AVAILABLE or INACTIVE or IN_USE"
                                    ) String status) {
}
