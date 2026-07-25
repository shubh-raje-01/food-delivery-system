package com.food_delivery_system.auth_service.repository;

import com.food_delivery_system.auth_service.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken> findByJti(String jti);

    List<RefreshToken> findBySubjectEmailAndRevokedFalseAndExpiresAtAfter(String subjectEmail, Instant now);
}
