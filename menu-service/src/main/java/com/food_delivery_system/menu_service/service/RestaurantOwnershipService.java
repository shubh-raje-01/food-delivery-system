package com.food_delivery_system.menu_service.service;

import com.food_delivery_system.menu_service.client.RestaurantClient;
import com.food_delivery_system.menu_service.client.RestaurantSummary;
import com.food_delivery_system.menu_service.enums.Role;
import com.food_delivery_system.menu_service.exception.DependentServiceUnavailableException;
import com.food_delivery_system.menu_service.exception.ResourceNotFoundException;
import com.food_delivery_system.menu_service.exception.UnauthorizedActionException;
import com.food_delivery_system.menu_service.security.AuthenticatedUser;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantOwnershipService {

    private final RestaurantClient restaurantClient;

    public void verifyOwnership(String restaurantUuid, AuthenticatedUser currentUser) {
        if (currentUser.role() == Role.ADMIN) {
            return;
        }

        RestaurantSummary restaurant = fetchRestaurant(restaurantUuid);

        if (!restaurant.ownerUuid().equals(currentUser.userId())) {
            throw new UnauthorizedActionException("You do not own this restaurant");
        }
    }

    private RestaurantSummary fetchRestaurant(String restaurantUuid) {
        try {
            return restaurantClient.getRestaurant(restaurantUuid);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Restaurant not found: " + restaurantUuid);
        } catch (FeignException e) {
            log.warn("restaurant-service call failed for uuid={}: {}", restaurantUuid, e.getMessage());
            throw new DependentServiceUnavailableException(
                    "Could not verify restaurant ownership right now — please try again shortly");
        }
    }

}
