package com.food_delivery_system.menu_service.repository;

import com.food_delivery_system.menu_service.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    Optional<MenuItem> findByUuid(String uuid);

    Page<MenuItem> findByRestaurantUuid(String restaurantUuid, Pageable pageable);

    List<MenuItem> findByCategoryId(Long categoryId);

}