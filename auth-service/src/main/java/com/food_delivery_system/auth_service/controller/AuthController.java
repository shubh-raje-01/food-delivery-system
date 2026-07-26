package com.food_delivery_system.auth_service.controller;

import com.food_delivery_system.auth_service.dto.request.*;
import com.food_delivery_system.auth_service.dto.response.MessageResponse;
import com.food_delivery_system.auth_service.security.service.CustomUserDetails;
import com.food_delivery_system.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(currentUser, request);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Password changed successfully. Please log in again on other devices.")
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("If an account with that email exists, a password reset link has been sent.")
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Password reset successfully. Please log in with your new password.")
                .build());
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Email verified successfully.")
                .build());
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.resendVerification(request);
        return ResponseEntity.ok(MessageResponse.builder()
                .message("If an account with that email exists and isn't verified yet, a new link has been sent.")
                .build());
    }

}
