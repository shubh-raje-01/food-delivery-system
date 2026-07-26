package com.food_delivery_system.auth_service.service;

import com.food_delivery_system.auth_service.exceptions.InvalidTokenException;
import com.food_delivery_system.auth_service.model.RefreshToken;
import com.food_delivery_system.auth_service.repository.RefreshTokenRepository;
import com.food_delivery_system.auth_service.security.jwt.GeneratedToken;
import com.food_delivery_system.auth_service.security.jwt.JwtService;
import com.food_delivery_system.auth_service.security.service.CustomUserDetails;
import com.food_delivery_system.auth_service.util.TokenHasher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final TokenHasher tokenHasher;

    @Transactional
    public GeneratedToken issue(CustomUserDetails userDetails) {
        GeneratedToken generated = jwtService.generateRefreshToken(userDetails);

        RefreshToken entity = new RefreshToken(
                generated.jti(),
                userDetails.getUsername(),
                userDetails.getUser().getRole(),
                tokenHasher.hash(generated.token()),
                generated.expiresAt()
        );
        refreshTokenRepository.save(entity);
        return generated;
    }

    @Transactional
    public RefreshToken validate(String rawToken) {
        boolean isValidRefreshToken;
        try {
            isValidRefreshToken = jwtService.isRefreshToken(rawToken) && !jwtService.isTokenExpired(rawToken);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        if (!isValidRefreshToken) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        String jti = jwtService.extractJti(rawToken);
        RefreshToken stored = refreshTokenRepository.findByJti(jti)
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid or expired"));

        if (stored.isRevoked()) {
            log.warn("Reuse of revoked refresh token detected for subject {} — revoking all active tokens", stored.getSubjectEmail());
            revokeAllForSubject(stored.getSubjectEmail());
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        if (!tokenHasher.matches(rawToken, stored.getTokenHash())) {
            throw new InvalidTokenException("Refresh token is invalid or expired");
        }

        return stored;
    }

    @Transactional
    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void revokeByRawToken(String rawToken) {
        String jti = jwtService.extractJti(rawToken);
        refreshTokenRepository.findByJti(jti).ifPresent(this::revoke);
    }

    @Transactional
    public void revokeAllForSubject(String subjectEmail) {
        List<RefreshToken> active = refreshTokenRepository
                .findBySubjectEmailAndRevokedFalseAndExpiresAtAfter(subjectEmail, Instant.now());
        active.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(active);
    }

}