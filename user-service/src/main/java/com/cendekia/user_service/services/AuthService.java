package com.cendekia.user_service.services;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cendekia.user_service.dtos.LoginRequestDTO;
import com.cendekia.user_service.utils.JwtUtil;

import io.jsonwebtoken.JwtException;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        UserService userService,
        JwtUtil jwtUtil,
        PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<String> authenticate(LoginRequestDTO loginRequestDTO) {
        Optional<String> token = userService
            .findByUid(loginRequestDTO.getUid())
            .filter(user -> passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword()))
            .map(user -> jwtUtil.generateToken(user.getUid(), user.getEmail(), user.getRole().toString()));
        return token;
    }



    public boolean validateToken(String token) {
        try {
            jwtUtil.validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
