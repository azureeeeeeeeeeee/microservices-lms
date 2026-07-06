package com.cendekia.user_service.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cendekia.user_service.enums.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtil {
    private final Key secretKey;
    private final long accessTokenExpired;
    private final long refreshTokenExpired;

    public JwtUtil(
        @Value("${jwt.secret}") String secretKey,
        @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration,
        @Value("${jwt.access-token-expiration}") long accessTokenExpiration
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(
            secretKey.getBytes(StandardCharsets.UTF_8)
        );
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpired = refreshTokenExpiration;
        this.refreshTokenExpired = accessTokenExpired;
    }

    public String generateAccessToken(UUID id, String email, String role) {
        Instant now = Instant.now();

        return Jwts.builder()
            .subject(id.toString())
            .claim("email", email)
            .claim("role", role)
            .claim("type", Token.ACCESS)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(accessTokenExpired)))
            .signWith(secretKey)
            .compact();
    }

    public String generateRefreshToken(UUID id) {
        Instant now = Instant.now();

        return Jwts.builder()
            .subject(id.toString())
            .claim("type", Token.REFRESH)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(refreshTokenExpired)))
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
