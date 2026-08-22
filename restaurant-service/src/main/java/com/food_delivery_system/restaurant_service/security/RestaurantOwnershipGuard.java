package com.food_delivery_system.restaurant_service.security;

import com.food_delivery_system.restaurant_service.enums.Role;
import com.food_delivery_system.restaurant_service.exception.UnauthorizedActionException;
import com.food_delivery_system.restaurant_service.model.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class RestaurantOwnershipGuard {

    public void assertOwnerOrAdmin(Restaurant restaurant, AuthenticatedUser currentUser) {
        boolean isOwner = restaurant.getOwnerUuid().equals(currentUser.userId());
        boolean isAdmin = currentUser.role() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedActionException("You do not own this restaurant");
        }
    }

}
