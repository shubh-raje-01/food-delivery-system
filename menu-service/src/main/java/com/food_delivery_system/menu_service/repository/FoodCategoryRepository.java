package com.food_delivery_system.menu_service.repository;

import com.food_delivery_system.menu_service.model.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Long> {

    List<FoodCategory> findByRestaurantUuidOrderByDisplayOrderAsc(String restaurantUuid);

    Optional<FoodCategory> findByRestaurantUuidAndName(String restaurantUuid, String name);

}