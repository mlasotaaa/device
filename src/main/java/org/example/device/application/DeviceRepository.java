package org.example.device.application;

import org.example.device.domain.model.Device;
import org.example.device.domain.model.DeviceBrand;
import org.example.device.domain.model.DeviceId;
import org.example.device.domain.model.DeviceState;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository {

    Device save(Device device);

    Optional<Device> findById(DeviceId id);

    List<Device> findAll();

    List<Device> findByState(DeviceState state);

    List<Device> findByBrand(DeviceBrand brand);

    void delete(DeviceId id);
}
