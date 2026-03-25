package org.example.device.infrastructure.persistence;

import org.example.device.domain.model.DeviceState;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("devices")
record DeviceEntity(@Id Long id,
                    @Column("device_id") UUID deviceId,
                    String name,
                    String brand,
                    DeviceState state,
                    @Column("created_at") Instant createdAt) {
}
