package com.food_delivery_system.auth_service.dto.response;

import com.food_delivery_system.auth_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String uuid;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private boolean emailVerified;

}
