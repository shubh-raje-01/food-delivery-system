package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.RestaurantImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantImageRepository extends JpaRepository<RestaurantImage, Long> {

    RestaurantImage findByRestaurantIdOrderByDisplayOrderAsc(Long restaurantId, int displayOrder);
}
