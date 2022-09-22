package de.hrw.xilab.controller;


import de.hrw.xilab.model.api.Device;
import de.hrw.xilab.services.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestApiController("/admin")
public class AdminController {

    public final DeviceService deviceService;

    public AdminController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }


    @PostMapping(path = "/new/device")
    public void postDevice(@RequestBody Optional<Device> device) {
        deviceService.save(device.orElseThrow());
    }

    @PostMapping(path = "/new/list")
    public void postDevices(@RequestBody Optional<List<Device>> device) {
        deviceService.saveAll(device.orElseThrow());
    }

    @PatchMapping(path = "/patch")
    public void patchDevice(@RequestBody Optional<Device> device) {
        deviceService.update(device.orElseThrow());
    }


    @DeleteMapping(path = "/remove/device")
    public void deleteDevice(@RequestParam String uuid) {
        deviceService.deleteByUuid(uuid);
    }

    @DeleteMapping(path = "/remove/list")
    public ResponseEntity<List<String>> deleteDeviceList(@RequestBody List<String> uuids) {
        var removed = deviceService.deleteByUuidList(uuids);
        return ResponseEntity.ok(removed);
    }


}
