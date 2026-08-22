package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.OpeningHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OpeningHoursRepository extends JpaRepository<OpeningHours, Long> {

    Optional<OpeningHours> findByRestaurantId(Long restaurantId);

}
