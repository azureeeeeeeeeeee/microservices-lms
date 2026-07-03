package com.cendekia.user_service.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {
    private final Key secretKey;
    
    public JwtUtil(
        @Value("${jwt.secret}") String secretKey
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(
            secretKey.getBytes(StandardCharsets.UTF_8)
        );
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String uid, String email, String role) {
        return io.jsonwebtoken.Jwts.builder()
            .claim("uid", uid)
            .claim("email", email)
            .claim("role", role)
            .signWith(secretKey)
            .compact();
    }

    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) secretKey)
                .build()
                .parseSignedClaims(token);
        } catch (JwtException e) {
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
