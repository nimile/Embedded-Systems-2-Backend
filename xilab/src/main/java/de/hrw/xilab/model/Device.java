package de.hrw.xilab.model;

import lombok.Data;

@Data
public class Device {
    private DeviceData deviceData;
    private LocationData locationData;
    private WaterSensorData waterSensorData;


}
