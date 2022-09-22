package de.hrw.xilab.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "DeviceWrapper")
public class DeviceWrapper {
    public DeviceWrapper() {

    }

    public static Device toDevice(DeviceWrapper wrapperDevice){
        var device = new Device();
        DeviceData deviceData = new DeviceData();
        LocationData locationData = new LocationData();
        WaterSensorData waterSensorData = new WaterSensorData();

        deviceData.setBattery(wrapperDevice.battery);
        deviceData.setName(wrapperDevice.name);
        deviceData.setUuid(wrapperDevice.uuid);

        locationData.setLatitude(wrapperDevice.latitude);
        locationData.setLongitude(wrapperDevice.longitude);

        waterSensorData.setMin(wrapperDevice.min);
        waterSensorData.setMax(wrapperDevice.max);
        waterSensorData.setCurrent(wrapperDevice.current);

        device.setDeviceData(deviceData);
        device.setLocationData(locationData);
        device.setWaterSensorData(waterSensorData);
        return device;
    }

    public static DeviceWrapper toDeviceWrapper(Device device){
        return new DeviceWrapper(device);
    }

    private DeviceWrapper(Device device){
        name = device.getDeviceData().getName();
        uuid = device.getDeviceData().getUuid();
        battery = device.getDeviceData().getBattery();

        latitude = device.getLocationData().getLatitude();
        longitude = device.getLocationData().getLongitude();


        max = device.getWaterSensorData().getMax();;
        min = device.getWaterSensorData().getMin();
        current = device.getWaterSensorData().getCurrent();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String uuid;
    private int battery;

    private long latitude;
    private long longitude;

    private int max;
    private int min;
    private int current;
}
