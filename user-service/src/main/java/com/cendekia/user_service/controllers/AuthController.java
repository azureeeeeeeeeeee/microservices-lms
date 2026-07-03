package com.cendekia.user_service.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.print.DocFlavor.STRING;

import org.apache.catalina.connector.Response;
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
import com.cendekia.user_service.dtos.register.RegisterRequestDTO;
import com.cendekia.user_service.dtos.register.RegisterResponseDTO;
import com.cendekia.user_service.dtos.user.GetUserResponseDTO;
import com.cendekia.user_service.dtos.user.UpdateUserRequestDTO;
import com.cendekia.user_service.dtos.user.UpdateUserResponseDTO;
import com.cendekia.user_service.enums.Role;
import com.cendekia.user_service.exceptions.EmailAlreadyExistsException;
import com.cendekia.user_service.exceptions.PasswordDoNotMatchException;
import com.cendekia.user_service.exceptions.UserNotFoundException;
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
        Optional<String> tokenOptional = authService.authenticate(loginRequestDTO);
        
        if (tokenOptional.isEmpty()) {
            log.warn("Invalid JWT");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO());
        }

        log.info("Token has been validated");
        String token = tokenOptional.get();
        return ResponseEntity.ok(new LoginResponseDTO("Login is successful", token));
        
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

    // @Operation(summary = "Get currently authenticated user")
    // @GetMapping("/me")
    // public ResponseEntity<GetUserResponseDTO> getCurrentUser() {
    //     log.info("Accessing get current user");
    //     String token = 
    //     Optional<String> tokeOptional = authService.extractClaims(null)
    // }
}
