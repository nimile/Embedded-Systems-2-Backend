package de.hrw.xilab.services;

import de.hrw.xilab.model.api.Device;
import de.hrw.xilab.model.wrapper.DeviceWrapper;
import de.hrw.xilab.repository.DeviceRepository;
import de.hrw.xilab.util.ModelUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static de.hrw.xilab.util.ModelUtils.toDeviceWrapperFromDevice;

@Service
public class DeviceService {

    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }


    public void save(Device device) {
        repository.save(toDeviceWrapperFromDevice(device));
    }

    public void saveAll(List<Device> devices) {
        var out = devices.stream()
                .map(ModelUtils::toDeviceWrapperFromDevice)
                .toList();
        repository.saveAll(out);
    }


    public List<Device> findAll() {
        return repository.findAll().stream()
                .map(ModelUtils::toDeviceFromDeviceWrapper)
                .toList();
    }

    public Device findByUuid(String uuid) {
        var device = repository.findByUuid(uuid).orElseThrow();
        return ModelUtils.toDeviceFromDeviceWrapper(device);
    }

    public List<Device> findAllByBatteryBelow(double battery) {
        return repository.findAll().stream()
                .filter(deviceWrapper -> deviceWrapper.getBattery() <= battery)
                .map(ModelUtils::toDeviceFromDeviceWrapper)
                .toList();
    }

    public List<Device> findAllByMinimumWaterReached() {
        return repository.findAll().stream()
                .filter(deviceWrapper -> deviceWrapper.getMin() >= deviceWrapper.getCurrent())
                .map(ModelUtils::toDeviceFromDeviceWrapper)
                .toList();
    }


    public List<String> deleteByUuidList(List<String> uuids) {
        List<String> removed = new ArrayList<>();
        uuids.forEach(uuid -> {
            try {
                deleteByUuid(uuid);
                removed.add(uuid);
            } catch (Exception ignored) {
            }
        });
        return removed;
    }

    public void deleteByUuid(String uuid) {
        var id = repository.findByUuid(uuid).orElseThrow().getId();
        repository.deleteById(id);
    }


    public void update(Device device) {
        String uuid = device.getDeviceData().getUuid();
        findByUuid(uuid);
        save(device);
    }

    public void update(String uuid, Optional<Integer> battery, Optional<Integer> waterlevel) {
        var dataset = repository.findByUuid(uuid).orElseThrow();
        battery.ifPresent(dataset::setBattery);
        waterlevel.ifPresent(dataset::setCurrent);
        repository.save(dataset);
    }








    public List<Device> filter(Optional<String> name,
                               Optional<Long> battery,
                               Optional<Double> latitude,
                               Optional<Double> longitude,
                               Optional<Long> min,
                               Optional<Long> max,
                               Optional<Long> current) {
        Predicate<DeviceWrapper> nameFilter = device -> true;
        Predicate<DeviceWrapper> batteryFilter = device -> true;
        Predicate<DeviceWrapper> latitudeFilter = device -> true;
        Predicate<DeviceWrapper> longitudeFilter = device -> true;
        Predicate<DeviceWrapper> minFilter = device -> true;
        Predicate<DeviceWrapper> maxFilter = device -> true;
        Predicate<DeviceWrapper> currentFilter = device -> true;

        if (name.isPresent()) {
            nameFilter = device -> device.getName().contains(name.get());
        }
        if (battery.isPresent()) {
            batteryFilter = device -> device.getBattery() <= battery.get();
        }
        if (latitude.isPresent()) {
            latitudeFilter = device -> device.getLatitude() == latitude.get();
        }
        if (longitude.isPresent()) {
            longitudeFilter = device -> device.getLongitude() == longitude.get();
        }
        if (max.isPresent()) {
            maxFilter = device -> device.getMax() == max.get();
        }
        if (min.isPresent()) {
            minFilter = device -> device.getMin() == min.get();
        }
        if (current.isPresent()) {
            currentFilter = device -> device.getCurrent() == current.get();
        }

        return repository.findAll().stream()
                .filter(nameFilter)
                .filter(batteryFilter)
                .filter(longitudeFilter)
                .filter(latitudeFilter)
                .filter(minFilter)
                .filter(maxFilter)
                .filter(currentFilter)
                .map(ModelUtils::toDeviceFromDeviceWrapper).toList();
    }

}
