package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByUuid(String uuid);
    List<Restaurant> findByOwnerUuid(String ownerUuid);

}
