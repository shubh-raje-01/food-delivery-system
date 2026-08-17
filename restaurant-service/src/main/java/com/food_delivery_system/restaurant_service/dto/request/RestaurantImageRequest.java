package com.food_delivery_system.restaurant_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantImageRequest {

    @NotBlank
    private String imageUrl;

    @Builder.Default
    private boolean primary = false;

    @Builder.Default
    private int displayOrder = 0;

}