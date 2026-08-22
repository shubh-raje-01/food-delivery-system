package com.food_delivery_system.restaurant_service.service;

import com.food_delivery_system.restaurant_service.model.Category;
import com.food_delivery_system.restaurant_service.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Set<Category> findOrCreateAll (Set<String> names){
        if (names == null || names.isEmpty()){
            return Set.of();
        }

        Set<Category> categories = new HashSet<>();
        for (String name : names) {
            String normalised = name.trim();
            Category category = categoryRepository.findByName(normalised)
                    .orElseGet(() -> categoryRepository.save(new Category(normalised)));

            categories.add(category);
        }

        return categories;
    }

}
