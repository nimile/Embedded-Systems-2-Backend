package de.hrw.xilab.controller;


import de.hrw.xilab.model.Device;
import de.hrw.xilab.services.DeviceService;
import de.hrw.xilab.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestApiController("/token")
public class TokenController {

    public final DeviceService deviceService;
    private final TokenService tokenService;

    public TokenController(DeviceService deviceService, TokenService tokenService) {
        this.deviceService = deviceService;
        this.tokenService = tokenService;
    }

    @PostMapping(path = "/create")
    public String token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @PostMapping(path = "/renew")
    public String renew(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

}
