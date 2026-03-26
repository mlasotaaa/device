package org.example.device.infrastructure.persistence;

import org.example.device.domain.model.*;
import org.springframework.stereotype.Component;

@Component
class DeviceMapper {

    DeviceEntity toEntity(Device device) {
        return new DeviceEntity(
                device.id(),
                device.deviceId().value(),
                device.deviceName().value(),
                device.deviceBrand().value(),
                device.deviceState(),
                device.createdAt()
        );
    }

    Device toDomain(DeviceEntity entity) {
        return new Device(
                entity.id(),
                new DeviceId(entity.deviceId()),
                new DeviceName(entity.name()),
                new DeviceBrand(entity.brand()),
                entity.state(),
                entity.createdAt()
        );
    }
}