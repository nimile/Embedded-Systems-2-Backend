package de.hrw.xilab.spring.controller;


import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.charset.StandardCharsets;
import de.hrw.xilab.spring.util.IOUtils;

import static de.hrw.xilab.spring.util.IOUtils.*;

@RestApiController("/code")
@CrossOrigin(origins = "http://localhost:3000")
public class SourceCodeController {

    private final String shared_utils;
    private final String backend_glue;
    private final String backend_loracom;
    private final String backend_main;

    private final String device_main;
    private final String device_lora;

    public SourceCodeController() {
        this.shared_utils = readFromResource("sourcecode/shared/utils.h");

        this.backend_glue = readFromResource("sourcecode/gateway/backendglue.h");
        this.backend_loracom = readFromResource("sourcecode/gateway/loracom.h");
        this.backend_main = readFromResource("sourcecode/gateway/LoRaGateway.ino");

        this.device_main = readFromResource("sourcecode/device/WaterMeasurement.ino");
        this.device_lora = readFromResource("sourcecode/device/loracom.ino");
    }

    @GetMapping(path = "/gateway/backendglue")
    public ResponseEntity<Resource> getBackendGlue() {
        byte[] result = backend_glue.getBytes(StandardCharsets.UTF_8);
        return buildFileDownloadResponseEntity(result, "backendglue.h");
    }

    @GetMapping(path = "/gateway/loracom")
    public ResponseEntity<Resource> getBackendLora() {
        byte[] result = backend_loracom.getBytes(StandardCharsets.UTF_8);
        return buildFileDownloadResponseEntity(result, "loracom.h");
    }

    @GetMapping(path = "/gateway/utils")
    public ResponseEntity<Resource> getBackendUtils() {
        byte[] result = shared_utils.getBytes(StandardCharsets.UTF_8);
        return buildFileDownloadResponseEntity(result, "utils.h");
    }

    @GetMapping(path = "/gateway/main")
    public ResponseEntity<Resource> getBackendMain() {
        byte[] result = backend_main.getBytes(StandardCharsets.UTF_8);
        return buildFileDownloadResponseEntity(result, "LoRaGateway.ino");
    }



    @GetMapping(path = "/device/main")
    public ResponseEntity<Resource> getDeviceMain() {
        byte[] result = device_main.getBytes(StandardCharsets.UTF_8);
        return IOUtils.buildFileDownloadResponseEntity(result, "WaterMeasurement.ino");
    }

    @GetMapping(path = "/device/utils")
    public ResponseEntity<Resource> getDeviceUtils() {
        byte[] result = shared_utils.getBytes(StandardCharsets.UTF_8);
        return IOUtils.buildFileDownloadResponseEntity(result, "utils.ino");
    }

    @GetMapping(path = "/device/lora")
    public ResponseEntity<Resource> getDeviceLora() {
        byte[] result = device_lora.getBytes(StandardCharsets.UTF_8);
        return IOUtils.buildFileDownloadResponseEntity(result, "loracom.ino");
    }
}
