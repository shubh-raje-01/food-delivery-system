package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Restaurant findByUuid(String uuid);
    Restaurant findByOwnerUuid(String ownerUuid);

}
