package de.hrw.xilab.controller;


import de.hrw.xilab.model.Device;
import de.hrw.xilab.services.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestApiController("/admin")
public class AdminController {
    // TODO Add login
    @Autowired
    public DeviceService deviceService;

    @GetMapping("/get")
    public ResponseEntity<Device> getDevice(@RequestParam String uuid){
        var result = deviceService.findByUuid(uuid);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/patch")
    public ResponseEntity<String> patchDevice(@RequestParam String uuid){
        //TODO: deviceService.update(uuid);
        return ResponseEntity.ok().build();
    }



    @DeleteMapping("/remove")
    public ResponseEntity<String> deleteDevice(@RequestParam String uuid){
        //TODO: deviceService.delete(uuid);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> deleteDeviceList(@RequestBody List<String> uuids){
        //TODO: deviceService.deleteAll(uuids);
        return ResponseEntity.ok().build();
    }



    @PostMapping("/new/device")
    public ResponseEntity<String> postDevice(@RequestBody Optional<Device> device){
        deviceService.save(device.orElseThrow());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new/list")
    public ResponseEntity<List<Device>> postDevices(@RequestBody Optional<List<Device>> device){
        deviceService.saveAll(device.orElseThrow());
        return ResponseEntity.ok().build();
    }
}
