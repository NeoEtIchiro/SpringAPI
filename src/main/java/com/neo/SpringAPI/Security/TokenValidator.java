package com.neo.SpringAPI.Security;

import java.util.Base64;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenValidator {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(TokenValidator.class);
    private final SecretKey key;

    public TokenValidator(@Value("${springapi.app.jwtSecret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT expired: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT invalid: {}", e.getMessage());
        }
        return false;
    }
}
