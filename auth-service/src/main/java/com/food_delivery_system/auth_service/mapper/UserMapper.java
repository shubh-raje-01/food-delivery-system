package com.food_delivery_system.auth_service.mapper;

import com.food_delivery_system.auth_service.dto.response.UserResponse;
import com.food_delivery_system.auth_service.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .uuid(user.getUuid())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .emailVerified(user.isEmailVerified())
                .build();
    }

}
