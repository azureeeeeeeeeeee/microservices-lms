package com.cendekia.user_service.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cendekia.user_service.dtos.user.UserResponseDTO;
import com.cendekia.user_service.exceptions.UserNotFoundException;
import com.cendekia.user_service.models.User;
import com.cendekia.user_service.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{uid}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String uid) {
        User user = userRepository.findByUid(uid)
                        .orElseThrow(() -> new UserNotFoundException("User not found : " + uid));

        UserResponseDTO response = new UserResponseDTO();

        response.setMessage("User fetched successfully");
        
        Map<String, String> data = new HashMap<>();

        data.put("uid", user.getUid());
        data.put("fullname", user.getFullname());
        data.put("role", user.getRole().toString());

        response.setData(data);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    
}
