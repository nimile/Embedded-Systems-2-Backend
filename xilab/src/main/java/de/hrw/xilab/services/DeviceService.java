package de.hrw.xilab.services;

import de.hrw.xilab.model.Device;
import de.hrw.xilab.model.DeviceWrapper;
import de.hrw.xilab.model.UpdateDeviceWrapper;
import de.hrw.xilab.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class DeviceService {

    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    public List<Device> findAll() {
        return repository.findAll().stream()
                .map(DeviceWrapper::toDevice)
                .toList();
    }

    public Device findByUuid(String uuid) {
        var device = repository.findByUuid(uuid).orElseThrow();
        return DeviceWrapper.toDevice(device);
    }

    public void save(Device device) {
        var out = DeviceWrapper.toDeviceWrapper(device);
        repository.save(out);
    }

    public void saveAll(List<Device> devices) {
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

    public String deleteByUuid(String uuid) {
        var id = repository.findByUuid(uuid).orElseThrow().getId();
        repository.deleteById(id);
        return uuid;
    }

    public void update(Device device) {
        var deviceWrapper = DeviceWrapper.toDeviceWrapper(device);
        var target = repository.findByUuid(deviceWrapper.getUuid()).orElseThrow();
        target = DeviceWrapper.updateDeviceWrapper(target, device);
        repository.save(target);
    }

    public void update(UpdateDeviceWrapper device) {
        var target = repository.findByUuid(device.getUuid()).orElseThrow();
        target = UpdateDeviceWrapper.getDevice(target, device);
        repository.save(target);
    }

    public List<Device> filter(Optional<String> name,
                               Optional<String> uuid,
                               Optional<Long> battery,
                               Optional<Double> latitude,
                               Optional<Double> longitude,
                               Optional<Long> min,
                               Optional<Long> max,
                               Optional<Long> current) {
        Predicate<DeviceWrapper> nameFilter = device -> true;
        Predicate<DeviceWrapper> uuidFilter = device -> true;
        Predicate<DeviceWrapper> batteryFilter = device -> true;
        Predicate<DeviceWrapper> latitudeFilter = device -> true;
        Predicate<DeviceWrapper> longitudeFilter = device -> true;
        Predicate<DeviceWrapper> minFilter = device -> true;
        Predicate<DeviceWrapper> maxFilter = device -> true;
        Predicate<DeviceWrapper> currentFilter = device -> true;

        if (name.isPresent()) {
            nameFilter = device -> device.getName().contains(name.get());
        }
        if (uuid.isPresent()) {
            uuidFilter = device -> device.getUuid().equals(uuid.get());
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
                .filter(uuidFilter)
                .filter(nameFilter)
                .filter(batteryFilter)
                .filter(longitudeFilter)
                .filter(latitudeFilter)
                .filter(minFilter)
                .filter(maxFilter)
                .filter(currentFilter)
                .map(DeviceWrapper::toDevice).toList();
    }
}
