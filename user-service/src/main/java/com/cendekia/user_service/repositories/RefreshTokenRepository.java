package com.cendekia.user_service.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cendekia.user_service.models.RefreshToken;
import com.cendekia.user_service.models.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    // List<RefreshToken> findByRevokedFalseAndExpiresAtBefore(Instant currentTime);
    // Page<RefreshToken> findByRevokedFalseAndExpiresAtBefore(LocalDateTime currentTime, Pageable pageable);
    // Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
    void deleteByUser(User user);
}
