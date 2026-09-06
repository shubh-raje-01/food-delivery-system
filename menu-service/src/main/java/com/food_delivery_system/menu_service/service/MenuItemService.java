package com.food_delivery_system.menu_service.service;

import com.food_delivery_system.menu_service.dto.request.CreateMenuItemRequest;
import com.food_delivery_system.menu_service.dto.request.UpdateMenuItemRequest;
import com.food_delivery_system.menu_service.dto.response.MenuItemResponse;
import com.food_delivery_system.menu_service.exception.ResourceNotFoundException;
import com.food_delivery_system.menu_service.mapper.MenuItemMapper;
import com.food_delivery_system.menu_service.model.FoodCategory;
import com.food_delivery_system.menu_service.model.MenuItem;
import com.food_delivery_system.menu_service.repository.MenuItemRepository;
import com.food_delivery_system.menu_service.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final FoodCategoryService foodCategoryService;
    private final MenuItemMapper menuItemMapper;

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Transactional
    public MenuItemResponse create(
            String restaurantUuid,
            CreateMenuItemRequest request,
            AuthenticatedUser currentUser
    ) {
        FoodCategory category = foodCategoryService.findOrCreate(restaurantUuid, request.getCategoryName());

        MenuItem item = MenuItem.builder()
                .restaurantUuid(restaurantUuid)
                .category(category)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .vegetarian(request.isVegetarian())
                .build();

        item = menuItemRepository.save(item);
        log.info("Menu item created: uuid={}, restaurantUuid={}", item.getUuid(), restaurantUuid);

        return menuItemMapper.toResponse(item);
    }

    @Transactional(readOnly = true)
    public MenuItemResponse getByUuid(String itemUuid) {
        return menuItemMapper.toResponse(findByUuidOrThrow(itemUuid));
    }

    @Transactional(readOnly = true)
    public Page<MenuItemResponse> listByRestaurant(String restaurantUuid, Pageable pageable) {
        return menuItemRepository.findByRestaurantUuid(restaurantUuid, pageable).map(menuItemMapper::toResponse);
    }

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Transactional
    public MenuItemResponse update(String restaurantUuid, String itemUuid, UpdateMenuItemRequest request, AuthenticatedUser currentUser) {
        MenuItem item = findByUuidOrThrow(itemUuid);
        assertBelongsToRestaurant(item, restaurantUuid);

        FoodCategory category = foodCategoryService.findOrCreate(restaurantUuid, request.getCategoryName());

        item.setCategory(category);
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setDiscountPrice(request.getDiscountPrice());
        item.setImageUrl(request.getImageUrl());
        item.setVegetarian(request.isVegetarian());

        item = menuItemRepository.save(item);
        log.info("Menu item updated: uuid={}", item.getUuid());

        return menuItemMapper.toResponse(item);
    }

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Transactional
    public MenuItemResponse setAvailability(String restaurantUuid, String itemUuid, boolean available, AuthenticatedUser currentUser) {
        MenuItem item = findByUuidOrThrow(itemUuid);
        assertBelongsToRestaurant(item, restaurantUuid);

        item.setAvailable(available);
        item = menuItemRepository.save(item);
        log.info("Menu item availability changed: uuid={}, available={}", item.getUuid(), available);

        return menuItemMapper.toResponse(item);
    }

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Transactional
    public void delete(String restaurantUuid, String itemUuid, AuthenticatedUser currentUser) {
        MenuItem item = findByUuidOrThrow(itemUuid);
        assertBelongsToRestaurant(item, restaurantUuid);

        menuItemRepository.delete(item);
        log.info("Menu item deleted: uuid={}", itemUuid);
    }

    private MenuItem findByUuidOrThrow(String itemUuid) {
        return menuItemRepository.findByUuid(itemUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + itemUuid));
    }

    private void assertBelongsToRestaurant(MenuItem item, String restaurantUuid) {
        if (!item.getRestaurantUuid().equals(restaurantUuid)) {
            throw new ResourceNotFoundException("Menu item not found: " + item.getUuid());
        }
    }

}
