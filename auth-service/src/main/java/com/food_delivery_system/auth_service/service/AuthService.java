package com.food_delivery_system.auth_service.service;

import com.food_delivery_system.auth_service.dto.request.*;
import com.food_delivery_system.auth_service.dto.response.AuthResponse;
import com.food_delivery_system.auth_service.dto.response.UserResponse;
import com.food_delivery_system.auth_service.enums.Role;
import com.food_delivery_system.auth_service.exceptions.*;
import com.food_delivery_system.auth_service.mapper.UserMapper;
import com.food_delivery_system.auth_service.model.PasswordResetToken;
import com.food_delivery_system.auth_service.model.RefreshToken;
import com.food_delivery_system.auth_service.model.User;
import com.food_delivery_system.auth_service.model.VerificationToken;
import com.food_delivery_system.auth_service.repository.PasswordResetTokenRepository;
import com.food_delivery_system.auth_service.repository.UserRepository;
import com.food_delivery_system.auth_service.repository.VerificationTokenRepository;
import com.food_delivery_system.auth_service.security.jwt.GeneratedToken;
import com.food_delivery_system.auth_service.security.jwt.JwtService;
import com.food_delivery_system.auth_service.security.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final UserMapper userMapper;

    @Value("${app.verification-token-expiration-ms}")
    private long verificationTokenExpirationMs;

    @Value("${app.reset-token-expiration-ms}")
    private long resetTokenExpirationMs;

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("An account with this email already exists");
        }

        if (request.getRole() == Role.ADMIN) {
            throw new InvalidCredentialsException("Cannot self-register with the ADMIN role");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        user = userRepository.save(user);

        issueVerificationToken(user);

        return userMapper.toResponse(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isEnabled()) {
            throw new AccountDisabledException("This account has been disabled. Contact support for help.");
        }

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in");
        }

        if (request.getRole() != null && request.getRole() != user.getRole()) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenService.validate(request.getRefreshToken());

        User user = userRepository.findByEmail(stored.getSubjectEmail())
                .orElseThrow(() -> new InvalidTokenException("Refresh token is invalid or expired"));

        if (!user.isEnabled()) {
            throw new AccountDisabledException("This account has been disabled. Contact support for help.");
        }

        refreshTokenService.revoke(stored);
        return issueTokens(user);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeByRawToken(request.getRefreshToken());
    }

    @Transactional
    public void changePassword(CustomUserDetails currentUser, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("New password and confirm password do not match");
        }

        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenService.revokeAllForSubject(user.getEmail());
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            passwordResetTokenRepository.findByUserIdAndUsedFalse(user.getId())
                    .ifPresent(existing -> {
                        existing.setUsed(true);
                        passwordResetTokenRepository.save(existing);
                    });

            String rawToken = UUID.randomUUID().toString();
            PasswordResetToken token = new PasswordResetToken(
                    rawToken, user.getId(), Instant.now().plusMillis(resetTokenExpirationMs));
            passwordResetTokenRepository.save(token);

            emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
        });
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("New password and confirm password do not match");
        }

        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("Password reset link is invalid or expired"));

        if (token.isUsed() || token.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Password reset link is invalid or expired");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        passwordResetTokenRepository.save(token);

        refreshTokenService.revokeAllForSubject(user.getEmail());
    }

    @Transactional
    public void verifyEmail(String rawToken) {
        VerificationToken token = verificationTokenRepository.findByToken(rawToken)
                .orElseThrow(() -> new InvalidTokenException("Verification link is invalid or expired"));

        if (token.isUsed() || token.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Verification link is invalid or expired");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);

        token.setUsed(true);
        verificationTokenRepository.save(token);
    }

    @Transactional
    public void resendVerification(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail())
                .filter(user -> !user.isEmailVerified())
                .ifPresent(this::issueVerificationToken);
    }

    public UserResponse getCurrentUser(CustomUserDetails currentUser) {
        User user = userRepository.findByEmail(currentUser.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    private void issueVerificationToken(User user) {
        verificationTokenRepository.findByUserIdAndUsedFalse(user.getId())
                .ifPresent(existing -> {
                    existing.setUsed(true);
                    verificationTokenRepository.save(existing);
                });

        String rawToken = UUID.randomUUID().toString();
        VerificationToken token = new VerificationToken(
                rawToken, user.getId(), Instant.now().plusMillis(verificationTokenExpirationMs));
        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(user.getEmail(), rawToken);
    }

    private AuthResponse issueTokens(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        GeneratedToken access = jwtService.generateAccessToken(userDetails);
        GeneratedToken refresh = refreshTokenService.issue(userDetails);

        return AuthResponse.builder()
                .accessToken(access.token())
                .refreshToken(refresh.token())
                .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                .user(userMapper.toResponse(user))
                .build();
    }

}
