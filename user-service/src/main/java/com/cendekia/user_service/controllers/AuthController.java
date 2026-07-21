package com.cendekia.user_service.controllers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.print.DocFlavor.STRING;

import org.apache.catalina.connector.Response;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cendekia.user_service.dtos.login.LoginRequestDTO;
import com.cendekia.user_service.dtos.login.LoginResponseDTO;
import com.cendekia.user_service.dtos.logout.LogoutRequestDTO;
import com.cendekia.user_service.dtos.logout.LogoutResponseDTO;
import com.cendekia.user_service.dtos.refresh.UpdateAccessTokenRequestDTO;
import com.cendekia.user_service.dtos.refresh.UpdateAccessTokenResponseDTO;
import com.cendekia.user_service.dtos.register.RegisterRequestDTO;
import com.cendekia.user_service.dtos.register.RegisterResponseDTO;
import com.cendekia.user_service.dtos.user.GetUserResponseDTO;
import com.cendekia.user_service.dtos.user.UpdateUserRequestDTO;
import com.cendekia.user_service.dtos.user.UpdateUserResponseDTO;
import com.cendekia.user_service.enums.Role;
import com.cendekia.user_service.exceptions.EmailAlreadyExistsException;
import com.cendekia.user_service.exceptions.InvalidCredentialsException;
import com.cendekia.user_service.exceptions.InvalidRefreshTokenException;
import com.cendekia.user_service.exceptions.PasswordDoNotMatchException;
import com.cendekia.user_service.exceptions.UserNotFoundException;
import com.cendekia.user_service.models.RefreshToken;
import com.cendekia.user_service.models.User;
import com.cendekia.user_service.repositories.RefreshTokenRepository;
import com.cendekia.user_service.repositories.UserRepository;
import com.cendekia.user_service.services.AuthService;
import com.cendekia.user_service.utils.JwtUtil;

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
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, PasswordEncoder passwordEncoder, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil) {
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
    }


    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public RegisterResponseDTO createUser(
        @Valid @RequestBody RegisterRequestDTO registerRequestDTO
    ) {
        log.info("Requesting into register");
        RegisterResponseDTO response = new RegisterResponseDTO();
        if (registerRequestDTO.getPassword() == null || !registerRequestDTO.getPassword().equals(registerRequestDTO.getConfirmPassword())) {
            log.warn("Given password do not match");
            throw new PasswordDoNotMatchException("Password and confirm password do not match");
        }
        
        log.info("Creating user. . .");
        User user = User.builder()
            .uid(registerRequestDTO.getUid())
            .fullname(registerRequestDTO.getFullname())
            .email(registerRequestDTO.getEmail())
            .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
            .role(Role.ADMIN)
            .build();
        
        
        userRepository.save(user);
        
        log.info("Creating response. . .");
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
        log.info("Requesting into login");
        log.info("Authenticating given JWT");
        Map<String, String> tokens = authService.authenticate(loginRequestDTO)
                    .orElseThrow(() -> new InvalidCredentialsException("Given credentials is invalid"));
        
        log.info("User {} authenticated successfully", loginRequestDTO.getUid());

        LoginResponseDTO response = new LoginResponseDTO("Login successful", tokens.get("Refresh Token"), tokens.get("Access Token"));

        return ResponseEntity.ok(response); 
    }
    
    @Operation(summary = "Update existing user")
    @PutMapping("/user/{uid}")
    public ResponseEntity<UpdateUserResponseDTO> updateUser(@PathVariable String uid, @Valid @RequestBody UpdateUserRequestDTO updateUserReqeustDTO) {
        log.info("Requesting into update user");
        User user = userRepository.findByUid(uid)
        .orElseThrow(() -> new UserNotFoundException("User not found with given UID : " + uid));
        
        log.info("Checking email");
        if (userRepository.existsByEmailAndUidNot(updateUserReqeustDTO.getEmail(), uid)) {
            String message = String.format(
                "Email already exists [%s]. Ignoring UID : [%s]",
                updateUserReqeustDTO.getEmail(), uid
            );
            throw new EmailAlreadyExistsException(message);
        }

        user.setEmail(updateUserReqeustDTO.getEmail());
        user.setFullname(updateUserReqeustDTO.getFullname());
        user.setRole(updateUserReqeustDTO.getRole());

        User updatedUser = userRepository.save(user);

        Map<String, String> data = new HashMap<>();
        data.put("uid", updatedUser.getUid());
        data.put("fullname", updatedUser.getFullname());

        UpdateUserResponseDTO response = new UpdateUserResponseDTO("User updated successfuly", data);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh Existing Access Token")
    @PostMapping("/token/refresh")
    public ResponseEntity<UpdateAccessTokenResponseDTO> refreshAccessToken(
        @Valid @RequestBody UpdateAccessTokenRequestDTO updateAccessTokenRequestDTO
    ) {
        log.info("Refreshing token for user");
        RefreshToken token = refreshTokenRepository.findByToken(updateAccessTokenRequestDTO.getToken())
                                            .orElseThrow(() -> new InvalidRefreshTokenException("Invalid Refresh Token"));

        if (token.getRevoked()) {
            throw new InvalidRefreshTokenException("Token is invalid !");
        }
        
        if (Instant.now().isAfter(token.getExpiresAt())) {
            throw new InvalidRefreshTokenException("Token is expired !");
        }

        
        User user = token.getUser();
        log.debug("User ID : {}", token.getUser());

        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail(), user.getRole().toString());

        UpdateAccessTokenResponseDTO response = new UpdateAccessTokenResponseDTO();

        Map<String, String> data = new HashMap<>();
        data.put("Access Token", newAccessToken);

        response.setMessage("Refresh access token is successful");
        response.setData(data);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/logout")
    public ResponseEntity<LogoutResponseDTO> logout(
        @Valid @RequestBody LogoutRequestDTO logoutRequestDTO
    ) {
        RefreshToken token = refreshTokenRepository.findByToken(logoutRequestDTO.getToken())
                                .orElseThrow(() -> new InvalidRefreshTokenException("Token invalid"));

        if (token.getRevoked()) {
            throw new InvalidRefreshTokenException("User already logged out");
        }

        if (Instant.now().isAfter(token.getExpiresAt())) {
            throw new InvalidRefreshTokenException("Invalid user");
        }
        
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        LogoutResponseDTO response = new LogoutResponseDTO();

        response.setMessage("Logout Successful");

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    

    // @Operation(summary = "Get currently authenticated user")
    // @GetMapping("/me")
    // public ResponseEntity<GetUserResponseDTO> getCurrentUser() {
    //     log.info("Accessing get current user");
    //     String token = 
    //     Optional<String> tokeOptional = authService.extractClaims(null)
    // }
}
