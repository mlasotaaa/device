package org.example.device.infrastructure.rest;

import org.example.device.domain.model.Device;

import java.util.List;
import java.util.UUID;

record DeviceResponse(UUID deviceId, String brand, String name, String state) {

    public static DeviceResponse of(Device device) {
        return new DeviceResponse(
                device.deviceId().value(),
                device.deviceBrand().value(),
                device.deviceName().value(),
                device.deviceState().name()
        );
    }

    public static List<DeviceResponse> of(List<Device> devices) {
        return devices.stream().map(DeviceResponse::of).toList();
    }
}
