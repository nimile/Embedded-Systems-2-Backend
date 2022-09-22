package de.hrw.xilab.controller;

import de.hrw.xilab.model.api.Device;
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

    @PatchMapping(path = "/{uuid}")
    public void updateDevice(@PathVariable String uuid,
                             @RequestParam Optional<Integer> battery,
                             @RequestParam Optional<Integer> waterLevel) {
        deviceService.update(uuid, battery, waterLevel);
    }

    @GetMapping(path = "/{uuid}")
    public ResponseEntity<Device> getDevice(@PathVariable String uuid) {
        return ResponseEntity.ok(deviceService.findByUuid(uuid));
    }

    @GetMapping(path = "/all")
    public ResponseEntity<List<Device>> getDevices() {
        return ResponseEntity.ok(deviceService.findAll());
    }

    @GetMapping(path = "/battery")
    public ResponseEntity<List<Device>> getDevicesBelowBattery(@RequestParam double value) {
        return ResponseEntity.ok(deviceService.findAllByBatteryBelow(value));
    }

    @GetMapping(path = "/minimum")
    public ResponseEntity<List<Device>> getDevicesBelowMinimum() {
        return ResponseEntity.ok(deviceService.findAllByMinimumWaterReached());
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

}
