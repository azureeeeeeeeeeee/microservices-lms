package com.cendekia.api_gateway.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    public JwtUtil(
        @Value("${jwt.secret}") String secretKey
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(
            secretKey.getBytes(StandardCharsets.UTF_8)
        );
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token);
        } catch (Exception e) {
            throw new JwtException("Invalid JWT");
        }
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
            .verifyWith((SecretKey) secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }


}
