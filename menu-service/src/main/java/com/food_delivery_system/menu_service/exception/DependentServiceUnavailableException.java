package com.food_delivery_system.menu_service.exception;

public class DependentServiceUnavailableException extends RuntimeException {
    public DependentServiceUnavailableException(String message) {
        super(message);
    }
}
