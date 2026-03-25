package org.example.device.application.command;

import org.example.device.domain.model.DeviceBrand;
import org.example.device.domain.model.DeviceName;
import org.example.device.domain.model.DeviceState;

public record CreateDeviceCommand(DeviceName deviceName,
                                  DeviceBrand deviceBrand,
                                  DeviceState deviceState) {
}
