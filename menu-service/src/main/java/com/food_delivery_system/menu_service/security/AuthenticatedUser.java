package com.food_delivery_system.menu_service.security;

import com.food_delivery_system.menu_service.enums.Role;

public record AuthenticatedUser(String userId, String email, Role role) {
}
