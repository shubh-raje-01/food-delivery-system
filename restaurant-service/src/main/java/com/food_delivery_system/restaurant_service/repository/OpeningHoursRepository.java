package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OpeningHoursRepository extends JpaRepository<OpeningHours, Long> {

    OpeningHours findByRestaurantId(Long restaurantId);

}
