package org.example.device.infrastructure.persistence;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface JdbcDeviceRepository extends ListCrudRepository<DeviceEntity, Long> {

    Optional<DeviceEntity> findByDeviceId(UUID deviceId);

    void deleteByDeviceId(UUID deviceId);

    List<DeviceEntity> findByBrand(String brand);

    List<DeviceEntity> findByState(String state);
}