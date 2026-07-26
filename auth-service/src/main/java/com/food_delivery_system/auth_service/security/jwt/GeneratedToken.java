package com.food_delivery_system.auth_service.security.jwt;

import java.time.Instant;

public record GeneratedToken(String token, String jti, Instant expiresAt) {
}
