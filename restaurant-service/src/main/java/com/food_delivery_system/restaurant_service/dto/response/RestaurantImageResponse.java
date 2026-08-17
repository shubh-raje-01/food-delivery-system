package com.food_delivery_system.restaurant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantImageResponse {

    private Long id;
    private String imageUrl;
    private boolean primary;
    private int displayOrder;

}