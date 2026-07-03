package com.cendekia.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cendekia.user_service.enums.Role;
import com.cendekia.user_service.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUid(String uid);
    List<User> findByRole(Role role);
    Boolean existsByEmail(String email);
    Boolean existsByEmailAndUidNot(String email, String uid);
    Boolean existsByUid(String uid);
}
