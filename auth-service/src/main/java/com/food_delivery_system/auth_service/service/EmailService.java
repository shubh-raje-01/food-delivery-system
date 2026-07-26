package com.food_delivery_system.auth_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendVerificationEmail(String toEmail, String token) {
        log.info("[STUB EMAIL] Verification link for {}: /api/v1/auth/verify-email?token={}", toEmail, token);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        log.info("[STUB EMAIL] Password reset link for {}: /api/v1/auth/reset-password?token={}", toEmail, token);
    }

}
