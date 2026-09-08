package com.food_delivery_system.order_service.repository;

import com.food_delivery_system.order_service.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByUuid(String uuid);

    Page<Order> findByCustomerUuid(String customerUuid, Pageable pageable);

    Page<Order> findByRestaurantUuid(String restaurantUuid, Pageable pageable);

}
