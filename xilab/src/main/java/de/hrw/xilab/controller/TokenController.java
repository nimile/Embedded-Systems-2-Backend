package de.hrw.xilab.controller;


import de.hrw.xilab.model.TokenResult;
import de.hrw.xilab.services.DeviceService;
import de.hrw.xilab.services.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestApiController("/token")
public class TokenController {

    public final DeviceService deviceService;
    private final TokenService tokenService;

    public TokenController(DeviceService deviceService, TokenService tokenService) {
        this.deviceService = deviceService;
        this.tokenService = tokenService;
    }

    @GetMapping(path = "/create")
    public TokenResult token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @GetMapping(path = "/renew")
    public TokenResult renew(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }
}
