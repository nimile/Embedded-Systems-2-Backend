package de.hrw.xilab.spring.controller;


import de.hrw.xilab.spring.model.api.Device;
import de.hrw.xilab.spring.services.DeviceService;
import de.hrw.xilab.spring.util.FileIO;
import de.hrw.xilab.spring.util.exceptions.GenericObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestApiController("/admin")
public class AdminController {

    private final String code;
    private final String watermeasurement;
    private final String loracom;
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
    public final DeviceService deviceService;

    public AdminController(DeviceService deviceService) {

        this.deviceService = deviceService;

        code = FileIO.readFromResource("sourcecode/main.ino");
        watermeasurement = FileIO.readFromResource("sourcecode/watermeasurement.h");
        loracom = FileIO.readFromResource("sourcecode/loracom.h");
   }


    @PostMapping(path = "/new/device")
    public ResponseEntity<Resource> postDevice(@RequestParam Optional<String> name,
                                               @RequestParam Optional<Integer> max,
                                               @RequestParam Optional<Integer> min) {

        Device device = buildDevice(name.orElseThrow(), min.orElseThrow(), max.orElseThrow());
        deviceService.save(device);

        String main = code.replace("$NAME$", device.getDeviceData().getName())
                .replace("$UUID$", device.getDeviceData().getUuid())
                .replace("$MAX$", "" + device.getWaterSensorData().getMax())
                .replace("$MIN$", "" + device.getWaterSensorData().getMin());

        byte[] result = main.getBytes(StandardCharsets.UTF_8);
        return buildFileResponseEntity(result, "main.ino");
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

    @GetMapping(path = "/code/main")
    public ResponseEntity<Resource> getSourceCode(@RequestParam Optional<String> name,
                                                  @RequestParam Optional<Long> max,
                                                  @RequestParam Optional<Long> min){
        String uuid = UUID.randomUUID().toString();
        String main = code.replace("$NAME$", name.orElseThrow())
                .replace("$MAX$", "" + max.orElseThrow())
                .replace("$MIN$", "" + min.orElseThrow())
                .replace("$UUID$", uuid);

        byte[] result = main.getBytes(StandardCharsets.UTF_8);
        return buildFileResponseEntity(result, "main.ino");
    }

    @GetMapping(path = "/code/lora")
    public ResponseEntity<Resource> getLoraSourceCode(){
        byte[] result = loracom.getBytes(StandardCharsets.UTF_8);
        return buildFileResponseEntity(result, "loracom.h");
    }

    @GetMapping(path = "/code/measurement")
    public ResponseEntity<Resource> getWaterMeasurementSourceCode(){
        byte[] result = watermeasurement.getBytes(StandardCharsets.UTF_8);
        return buildFileResponseEntity(result, "watermeasurement.h");
    }

    private Device buildDevice(String name, int min, int max){
        Device device = new Device();

        device.getWaterSensorData().setMin(min);
        device.getWaterSensorData().setMax(max);
        device.getDeviceData().setName(name);
        UUID uuid = UUID.randomUUID();
        device.getDeviceData().setUuid(uuid.toString());
        return device;
    }

    private ResponseEntity<Resource> buildFileResponseEntity(byte[] data, String filename){

        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"" + filename + "\"")
                .contentLength(data.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping(path = "/test")
    public void testIt(@RequestBody Device device){
        throw new GenericObjectException("Dummy", device);
    }
}
