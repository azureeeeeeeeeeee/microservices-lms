package com.cendekia.user_service.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cendekia.user_service.enums.Role;
import com.cendekia.user_service.models.User;
import com.cendekia.user_service.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUid(String uid) {
        return userRepository.findByUid(uid);
    }

    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
