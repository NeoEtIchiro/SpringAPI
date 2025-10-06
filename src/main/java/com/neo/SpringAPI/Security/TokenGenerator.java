package com.neo.SpringAPI.Security;

import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class TokenGenerator {

    private final SecretKey key;
    private final int jwtExpirationMs;

    public TokenGenerator(@Value("${springapi.app.jwtSecret}") String secret,
                          @Value("${springapi.app.jwtExpirationMs}") int expMs) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpirationMs = expMs;
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key) // algo déduit de la taille
                .compact();
    }
}