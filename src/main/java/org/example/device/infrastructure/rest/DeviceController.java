package org.example.device.infrastructure.rest;

import jakarta.validation.Valid;
import org.example.device.application.DeviceService;
import org.example.device.application.command.CreateDeviceCommand;
import org.example.device.application.command.UpdateDeviceCommand;
import org.example.device.domain.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devices")
class DeviceController {

    private final DeviceService deviceService;

    @Autowired
    DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    DeviceResponse createDevice(@RequestBody @Valid CreateDeviceRequest createDeviceRequest) {
        return DeviceResponse.of(deviceService.createDevice(
                new CreateDeviceCommand(
                        Optional.ofNullable(createDeviceRequest.name()).map(DeviceName::new).orElse(null),
                        Optional.ofNullable(createDeviceRequest.brand()).map(DeviceBrand::new).orElse(null),
                        Optional.ofNullable(createDeviceRequest.status()).map(DeviceState::valueOf).orElse(null)
                )
        ));
    }

    @GetMapping("/{id}")
    DeviceResponse getDevice(@PathVariable UUID id) {
        return DeviceResponse.of(deviceService.getDevice(new DeviceId(id)));
    }

    @GetMapping
    List<DeviceResponse> getAllDevices(@RequestParam(required = false) String state,
                                       @RequestParam(required = false) String brand) {
        if (state != null) {
            return DeviceResponse.of(deviceService.getDevicesByState(DeviceState.valueOf(state)));
        }
        if (brand != null) {
            return DeviceResponse.of(deviceService.getDevicesByBrand(new DeviceBrand(brand)));
        }
        return DeviceResponse.of(deviceService.getAllDevices());
    }

    @PutMapping("/{id}")
    DeviceResponse updateDevice(@PathVariable UUID id, @RequestBody UpdateDeviceRequest updateDeviceRequest) {
        return DeviceResponse.of(deviceService.updateDevice(
                new DeviceId(id),
                new UpdateDeviceCommand(
                        Optional.ofNullable(updateDeviceRequest.name()).map(DeviceName::new).orElse(null),
                        Optional.ofNullable(updateDeviceRequest.brand()).map(DeviceBrand::new).orElse(null),
                        Optional.ofNullable(updateDeviceRequest.status()).map(DeviceState::valueOf).orElse(null)
                )
        ));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDevice(@PathVariable UUID id) {
        deviceService.deleteDevice(new DeviceId(id));
    }
}
