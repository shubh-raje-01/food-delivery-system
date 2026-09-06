package com.food_delivery_system.menu_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service")
public interface RestaurantClient {

    @GetMapping("/api/v1/restaurants/{uuid}")
    RestaurantSummary getRestaurant(@PathVariable("uuid") String uuid);

}
