package de.hrw.xilab.model;

import lombok.Data;


@Data
public class UpdateDeviceWrapper {
    private String uuid;
    private int battery;
    private int current;

    public static DeviceWrapper getDevice(DeviceWrapper deviceWrapper, UpdateDeviceWrapper updateDeviceWrapper) {
        deviceWrapper.setCurrent(updateDeviceWrapper.current);
        deviceWrapper.setBattery(updateDeviceWrapper.battery);
        return deviceWrapper;
    }
}
