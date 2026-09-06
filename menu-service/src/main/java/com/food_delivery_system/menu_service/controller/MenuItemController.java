package com.food_delivery_system.menu_service.controller;

import com.food_delivery_system.menu_service.dto.request.CreateMenuItemRequest;
import com.food_delivery_system.menu_service.dto.request.UpdateMenuItemRequest;
import com.food_delivery_system.menu_service.dto.response.MenuItemResponse;
import com.food_delivery_system.menu_service.security.AuthenticatedUser;
import com.food_delivery_system.menu_service.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuItemService menuItemService;

    @PostMapping("/api/v1/restaurants/{restaurantUuid}/menu-items")
    public ResponseEntity<MenuItemResponse> create(
            @PathVariable String restaurantUuid,
            @Valid @RequestBody CreateMenuItemRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        MenuItemResponse response = menuItemService.create(restaurantUuid, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/menu-items/{itemUuid}")
    public ResponseEntity<MenuItemResponse> getByUuid(@PathVariable String itemUuid) {
        return ResponseEntity.ok(menuItemService.getByUuid(itemUuid));
    }

    @GetMapping("/api/v1/restaurants/{restaurantUuid}/menu-items")
    public ResponseEntity<Page<MenuItemResponse>> listByRestaurant(
            @PathVariable String restaurantUuid,
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(menuItemService.listByRestaurant(restaurantUuid, pageable));
    }

    @PutMapping("/api/v1/restaurants/{restaurantUuid}/menu-items/{itemUuid}")
    public ResponseEntity<MenuItemResponse> update(
            @PathVariable String restaurantUuid,
            @PathVariable String itemUuid,
            @Valid @RequestBody UpdateMenuItemRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return ResponseEntity.ok(menuItemService.update(restaurantUuid, itemUuid, request, currentUser));
    }

    @PatchMapping("/api/v1/restaurants/{restaurantUuid}/menu-items/{itemUuid}/availability")
    public ResponseEntity<MenuItemResponse> setAvailability(
            @PathVariable String restaurantUuid,
            @PathVariable String itemUuid,
            @RequestParam boolean available,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return ResponseEntity.ok(menuItemService.setAvailability(restaurantUuid, itemUuid, available, currentUser));
    }

    @DeleteMapping("/api/v1/restaurants/{restaurantUuid}/menu-items/{itemUuid}")
    public ResponseEntity<Void> delete(
            @PathVariable String restaurantUuid,
            @PathVariable String itemUuid,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        menuItemService.delete(restaurantUuid, itemUuid, currentUser);
        return ResponseEntity.noContent().build();
    }

}
