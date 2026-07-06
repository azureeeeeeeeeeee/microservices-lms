package com.cendekia.user_service.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cendekia.user_service.models.RefreshToken;
import com.cendekia.user_service.models.User;
import com.cendekia.user_service.repositories.RefreshTokenRepository;


@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenrRepository;
    private final long refreshTokenExpiration;

    public RefreshTokenService(
        RefreshTokenRepository refreshTokenrRepository,
        @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.refreshTokenrRepository = refreshTokenrRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
        System.out.println("Refresh expiration = " + refreshTokenExpiration);

    }

    public RefreshToken create(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        Instant now = Instant.now();

        refreshToken.setRevoked(false);
        refreshToken.setToken(token);
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(
            now.plusMillis(refreshTokenExpiration)
        );
        refreshToken.setCreatedAt(now);
        refreshToken.setUpdatedAt(now);

        return refreshTokenrRepository.save(refreshToken);
    }  
}
