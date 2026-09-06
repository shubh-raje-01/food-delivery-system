package com.food_delivery_system.menu_service.mapper;

import com.food_delivery_system.menu_service.dto.response.FoodCategoryResponse;
import com.food_delivery_system.menu_service.dto.response.MenuItemResponse;
import com.food_delivery_system.menu_service.model.FoodCategory;
import com.food_delivery_system.menu_service.model.MenuItem;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    public MenuItemResponse toResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .uuid(item.getUuid())
                .restaurantUuid(item.getRestaurantUuid())
                .category(toCategoryResponse(item.getCategory()))
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .discountPrice(item.getDiscountPrice())
                .available(item.isAvailable())
                .vegetarian(item.isVegetarian())
                .imageUrl(item.getImageUrl())
                .createdAt(item.getCreatedAt())
                .build();
    }

    private FoodCategoryResponse toCategoryResponse(FoodCategory category) {
        if (category == null) {
            return null;
        }
        return FoodCategoryResponse.builder()
                .uuid(category.getUuid())
                .name(category.getName())
                .displayOrder(category.getDisplayOrder())
                .build();
    }

}