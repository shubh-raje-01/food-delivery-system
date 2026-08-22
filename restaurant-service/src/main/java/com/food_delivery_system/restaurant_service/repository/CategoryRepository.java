package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

}
