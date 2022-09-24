package de.hrw.xilab.spring.controller;


import de.hrw.xilab.spring.model.api.Device;
import de.hrw.xilab.spring.services.DeviceService;
import de.hrw.xilab.spring.util.exceptions.GenericObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestApiController("/admin")
public class AdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
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

    @GetMapping(path = "/test")
    public void testIt(@RequestBody Device device){
        throw new GenericObjectException("Dummy", device);
    }
}
