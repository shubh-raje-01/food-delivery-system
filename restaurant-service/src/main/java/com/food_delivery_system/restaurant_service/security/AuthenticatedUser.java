package com.food_delivery_system.restaurant_service.security;

import com.food_delivery_system.restaurant_service.enums.Role;

public record AuthenticatedUser(String userId, String email, Role role) {
}
