package com.food_delivery_system.menu_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {

    private String uuid;
    private String restaurantUuid;
    private FoodCategoryResponse category;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private boolean available;
    private boolean vegetarian;
    private String imageUrl;
    private Instant createdAt;

}