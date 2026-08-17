package com.food_delivery_system.restaurant_service.exception;

public class InvalidRestaurantStateException extends RuntimeException {
    public InvalidRestaurantStateException(String message) {
        super(message);
    }
}
