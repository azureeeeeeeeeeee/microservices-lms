package com.cendekia.user_service.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cendekia.user_service.dtos.login.LoginRequestDTO;
import com.cendekia.user_service.repositories.RefreshTokenRepository;
import com.cendekia.user_service.utils.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
        UserService userService,
        JwtUtil jwtUtil,
        PasswordEncoder passwordEncoder,
        RefreshTokenService refreshTokenService
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public Optional<Map<String, String>> authenticate(LoginRequestDTO loginRequestDTO) {

        return userService
                .findByUid(loginRequestDTO.getUid())
                .filter(user -> passwordEncoder.matches(
                        loginRequestDTO.getPassword(),
                        user.getPassword()))
                .map(user -> {

                    String accessToken = jwtUtil.generateAccessToken(
                            user.getId(),
                            user.getEmail(),
                            user.getRole().name()
                    );

                    String refreshToken = jwtUtil.generateRefreshToken(
                            user.getId()
                    );

                    refreshTokenService.create(user, refreshToken);

                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("Access Token", accessToken);
                    tokens.put("Refresh Token", refreshToken);

                    return tokens;

                });
    }



    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims extractClaims(String token) {
        return jwtUtil.extractClaims(token);
    }
}
