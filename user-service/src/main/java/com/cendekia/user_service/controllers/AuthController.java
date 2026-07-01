package com.cendekia.user_service.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cendekia.user_service.dtos.login.LoginRequestDTO;
import com.cendekia.user_service.dtos.login.LoginResponseDTO;
import com.cendekia.user_service.dtos.register.RegisterRequestDTO;
import com.cendekia.user_service.dtos.register.RegisterResponseDTO;
import com.cendekia.user_service.enums.Role;
import com.cendekia.user_service.exceptions.PasswordDoNotMatchException;
import com.cendekia.user_service.models.User;
import com.cendekia.user_service.repositories.UserRepository;
import com.cendekia.user_service.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public RegisterResponseDTO createUser(
        @Valid @RequestBody RegisterRequestDTO registerRequestDTO
    ) {
        RegisterResponseDTO response = new RegisterResponseDTO();
        if (registerRequestDTO.getPassword() == null || !registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
            throw new PasswordDoNotMatchException("Password and confirm password do not match");
        }

        User user = User.builder()
            .uid(registerRequestDTO.getUid())
            .fullname(registerRequestDTO.getFullname())
            .email(registerRequestDTO.getEmail())
            .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
            .role(Role.ADMIN)
            .build();

        
        userRepository.save(user);

        response.setMessage("Register is successful");
        Map<String, String> data = new HashMap<>();
        data.put("uid", user.getUid());
        data.put("email", user.getEmail());

        response.setData(data);

        return response;
    }

    @Operation(summary = "Login a user and get JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);

        if (tokenOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO());
        }

        String token = tokenOptional.get();
        return ResponseEntity.ok(new LoginResponseDTO("Login is successful", token));
        
    }
}
