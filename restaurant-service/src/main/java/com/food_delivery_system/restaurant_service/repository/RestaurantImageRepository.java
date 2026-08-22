package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.RestaurantImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantImageRepository extends JpaRepository<RestaurantImage, Long> {

    List<RestaurantImage> findByRestaurantIdOrderByDisplayOrderAsc(Long restaurantId);
}
