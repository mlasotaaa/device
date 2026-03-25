package org.example.device.application;

import org.example.device.application.command.CreateDeviceCommand;
import org.example.device.application.command.UpdateDeviceCommand;
import org.example.device.domain.model.Device;
import org.example.device.domain.model.DeviceBrand;
import org.example.device.domain.model.DeviceId;
import org.example.device.domain.model.DeviceState;

import java.util.List;

public interface DeviceService {

    Device createDevice(CreateDeviceCommand command);

    Device updateDevice(DeviceId deviceId, UpdateDeviceCommand command);

    Device getDevice(DeviceId deviceId);

    /*
        TODO: Consider implementing a pagination mechanism in the future to improve performance and reduce memory usage.
     */
    List<Device> getAllDevices();

    /*
        TODO: Consider implementing a pagination mechanism in the future to improve performance and reduce memory usage.
    */
    List<Device> getDevicesByBrand(DeviceBrand brand);

    /*
        TODO: Consider implementing a pagination mechanism in the future to improve performance and reduce memory usage.
    */
    List<Device> getDevicesByState(DeviceState deviceState);

    void deleteDevice(DeviceId deviceId);
}
