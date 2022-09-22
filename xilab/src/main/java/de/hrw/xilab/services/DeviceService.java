package de.hrw.xilab.services;

import de.hrw.xilab.model.Device;
import de.hrw.xilab.model.DeviceWrapper;
import de.hrw.xilab.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository repository;

    public DeviceService(){

    }

    public List<Device> findAll(){
        return repository.findAll().stream()
                .map(DeviceWrapper::toDevice)
                .toList();
    }

    public Device findByUuid(String uuid){
        var device = repository.findByUuid(uuid).orElseThrow();
        return DeviceWrapper.toDevice(device);
    }

    public void save(Device device){
        var out = DeviceWrapper.toDeviceWrapper(device);
        repository.save(out);
    }

    public void saveAll(List<Device> devices){
        var out = devices.stream()
                .map(DeviceWrapper::toDeviceWrapper)
                .toList();
        repository.saveAll(out);
    }

    public List<Device> findAllByBattery(double battery) {
        return repository.findAll().stream()
                .filter(deviceWrapper -> deviceWrapper.getBattery() <= battery)
                .map(DeviceWrapper::toDevice)
                .toList();
    }

    public List<Device> findAllByMinimumWaterReached() {
        return repository.findAll().stream()
                .filter(deviceWrapper -> deviceWrapper.getMin() >= deviceWrapper.getCurrent())
                .map(DeviceWrapper::toDevice)
                .toList();
    }
}
