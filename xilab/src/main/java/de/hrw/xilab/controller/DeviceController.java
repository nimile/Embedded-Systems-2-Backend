package de.hrw.xilab.controller;

import de.hrw.xilab.model.Device;
import de.hrw.xilab.model.UpdateDeviceWrapper;
import de.hrw.xilab.services.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestApiController("/device")
public class DeviceController {

    public final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Device>> getDevices() {
        var result = deviceService.findAll();
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/battery")
    public ResponseEntity<List<Device>> getDevicesBelowBattery(@RequestParam double value) {
        var result = deviceService.findAllByBattery(value);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/minimum")
    public ResponseEntity<List<Device>> getDevicesBelowMinimum() {
        var result = deviceService.findAllByMinimumWaterReached();
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Device> getDevice(@PathVariable Optional<String> id) {
        var deviceId = id.orElseThrow();
        var result = deviceService.findByUuid(deviceId);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/filter")
    public ResponseEntity<List<Device>> filter(@RequestParam Optional<String> name,
                                               @RequestParam Optional<Long> battery,
                                               @RequestParam Optional<Double> latitude,
                                               @RequestParam Optional<Double> longitude,
                                               @RequestParam Optional<Long> min,
                                               @RequestParam Optional<Long> max,
                                               @RequestParam Optional<Long> current) {
        return ResponseEntity.ok(deviceService.filter(name, battery, latitude, longitude, min, max, current));
    }

    @PatchMapping(path = "/update")
    public void updateDevice(@RequestBody UpdateDeviceWrapper updateDeviceWrapper) {
        deviceService.update(updateDeviceWrapper);
    }
}
