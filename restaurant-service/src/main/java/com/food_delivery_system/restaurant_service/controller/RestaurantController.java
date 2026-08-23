package com.food_delivery_system.restaurant_service.controller;

import com.food_delivery_system.restaurant_service.dto.request.CreateRestaurantRequest;
import com.food_delivery_system.restaurant_service.dto.request.UpdateRestaurantRequest;
import com.food_delivery_system.restaurant_service.security.AuthenticatedUser;
import com.food_delivery_system.restaurant_service.service.RestaurantService;
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
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateRestaurantRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurantService.create(request, currentUser));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> getByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(restaurantService.getByUuid(uuid));
    }

    @GetMapping
    public ResponseEntity<Page<?>> list(
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        return ResponseEntity.ok(restaurantService.list(pageable));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(
            @PathVariable String uuid,
            @Valid @RequestBody UpdateRestaurantRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        return ResponseEntity.ok(restaurantService.update(uuid, request, currentUser));
    }

}
