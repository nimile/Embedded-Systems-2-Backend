package de.hrw.xilab.services;

import de.hrw.xilab.model.TokenResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${security.expiration}")
    private int expiration;
    private final JwtEncoder jwtEncoder;

    public TokenService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public TokenResult generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiration = now.plus(this.expiration, ChronoUnit.DAYS);
        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        String issuer = "self";
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("scope", scope)
                .build();

        TokenResult result = new TokenResult();
        result.setToken(this.jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue());
        result.setExpiration(expiration);
        result.setIssuedAt(now);
        result.setScope(scope);
        result.setIssuer(issuer);
        return result;
    }

}
