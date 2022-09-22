package de.hrw.xilab.controller;

import de.hrw.xilab.model.Device;
import de.hrw.xilab.model.UpdateDeviceWrapper;
import de.hrw.xilab.services.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestApiController("/test")
public class TestController {

    public final DeviceService deviceService;

    public TestController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping(path = "/wrong_model")
    public ResponseEntity<String> getDevices(@RequestBody Device device) {
        System.out.println(device);
        return ResponseEntity.ok("ACK");
    }
}
