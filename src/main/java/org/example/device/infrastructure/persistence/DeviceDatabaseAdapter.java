package org.example.device.infrastructure.persistence;

import org.example.device.application.DeviceRepository;
import org.example.device.domain.model.Device;
import org.example.device.domain.model.DeviceBrand;
import org.example.device.domain.model.DeviceId;
import org.example.device.domain.model.DeviceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
class DeviceDatabaseAdapter implements DeviceRepository {

    private final JdbcDeviceRepository jdbcDeviceRepository;
    private final DeviceMapper mapper;

    @Autowired
    public DeviceDatabaseAdapter(JdbcDeviceRepository jdbcDeviceRepository,
                                 DeviceMapper mapper) {
        this.jdbcDeviceRepository = jdbcDeviceRepository;
        this.mapper = mapper;
    }

    @Override
    public Device save(Device device) {
        DeviceEntity entity = mapper.toEntity(device);

        DeviceEntity saved = jdbcDeviceRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Device> findById(DeviceId id) {
        return jdbcDeviceRepository.findByDeviceId(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Device> findAll() {
        return jdbcDeviceRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Device> findByState(DeviceState state) {
        return jdbcDeviceRepository.findByState(state.name()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Device> findByBrand(DeviceBrand brand) {
        return jdbcDeviceRepository.findByBrand(brand.value()).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(DeviceId id) {
        jdbcDeviceRepository.deleteByDeviceId(id.value());
    }
}