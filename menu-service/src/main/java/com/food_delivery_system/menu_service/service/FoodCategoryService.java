package com.food_delivery_system.menu_service.service;

import com.food_delivery_system.menu_service.model.FoodCategory;
import com.food_delivery_system.menu_service.repository.FoodCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FoodCategoryService {

    private final FoodCategoryRepository foodCategoryRepository;

    @Transactional
    public FoodCategory findOrCreate(String restaurantUuid, String categoryName) {
        String normalized = categoryName.trim();
        return foodCategoryRepository.findByRestaurantUuidAndName(restaurantUuid, normalized)
                .orElseGet(() -> foodCategoryRepository.save(
                        FoodCategory.builder()
                                .restaurantUuid(restaurantUuid)
                                .name(normalized)
                                .build()
                ));
    }

}
