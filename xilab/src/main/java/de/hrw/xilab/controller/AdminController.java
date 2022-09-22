package de.hrw.xilab.controller;


import de.hrw.xilab.model.Device;
import de.hrw.xilab.model.DeviceWrapper;
import de.hrw.xilab.services.DeviceService;
import de.hrw.xilab.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestApiController("/admin")
public class AdminController {

    public final DeviceService deviceService;
    private final TokenService tokenService;

    public AdminController(DeviceService deviceService, TokenService tokenService) {
        this.deviceService = deviceService;
        this.tokenService = tokenService;
    }

    @PostMapping(path = "/token")
    public String token(Authentication authentication){
        return tokenService.generateToken(authentication);
    }


    @GetMapping(path = "/get")
    public ResponseEntity<Device> getDevice(@RequestParam String uuid){
        var result = deviceService.findByUuid(uuid);
        return ResponseEntity.ok(result);
    }

    @GetMapping(path = "/list")
    public ResponseEntity<List<Device>> listDevice(){
        var result = deviceService.findAll();
        return ResponseEntity.ok(result);
    }

    @PatchMapping(path = "/patch")
    public ResponseEntity<String> patchDevice(@RequestBody Optional<Device> device){
        deviceService.update(device.orElseThrow());
        return ResponseEntity.ok().build();
    }


    @DeleteMapping(path = "/remove/device")
    public ResponseEntity<String> deleteDevice(@RequestParam String uuid){
        var removed = deviceService.deleteByUuid(uuid);
        return ResponseEntity.ok(removed);
    }

    @DeleteMapping(path = "/remove/list")
    public ResponseEntity<List<String>> deleteDeviceList(@RequestBody List<String> uuids){
        var removed = deviceService.deleteByUuidList(uuids);
        return ResponseEntity.ok(removed);
    }


    @PostMapping(path = "/new/device")
    public ResponseEntity<String> postDevice(@RequestBody Optional<Device> device){
        deviceService.save(device.orElseThrow());
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/new/list")
    public ResponseEntity<List<Device>> postDevices(@RequestBody Optional<List<Device>> device){
        deviceService.saveAll(device.orElseThrow());
        return ResponseEntity.ok().build();
    }
}
