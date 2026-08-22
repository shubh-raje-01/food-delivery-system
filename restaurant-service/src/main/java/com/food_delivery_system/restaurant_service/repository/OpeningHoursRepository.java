package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpeningHoursRepository extends JpaRepository<OpeningHours, Long> {

    List<OpeningHours> findByRestaurantId(Long restaurantId);

    void deleteByRestaurantId(Long restaurantId);

}
