package com.food_delivery_system.restaurant_service.repository;

import com.food_delivery_system.restaurant_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByName(String name);

}
