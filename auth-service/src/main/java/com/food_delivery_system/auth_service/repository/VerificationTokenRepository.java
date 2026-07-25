package com.food_delivery_system.auth_service.repository;

import com.food_delivery_system.auth_service.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUserIdAndUsedFalse(Long userId);

}
