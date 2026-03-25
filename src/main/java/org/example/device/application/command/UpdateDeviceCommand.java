package org.example.device.application.command;

import org.example.device.domain.model.DeviceBrand;
import org.example.device.domain.model.DeviceName;
import org.example.device.domain.model.DeviceState;

public record UpdateDeviceCommand(DeviceName name, DeviceBrand brand, DeviceState state) {
}
