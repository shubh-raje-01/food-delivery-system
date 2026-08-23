package com.food_delivery_system.restaurant_service.controller;

import com.food_delivery_system.restaurant_service.dto.request.RestaurantImageRequest;
import com.food_delivery_system.restaurant_service.security.AuthenticatedUser;
import com.food_delivery_system.restaurant_service.service.RestaurantImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants/{uuid}/images")
@RequiredArgsConstructor
public class RestaurantImageController {

    private final RestaurantImageService restaurantImageService;

    @PostMapping
    public ResponseEntity<?> addImage(
            @PathVariable String uuid,
            @Valid @RequestBody RestaurantImageRequest request,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        restaurantImageService.addImage(uuid, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{imageId}/primary")
    public ResponseEntity<?> setPrimary(
            @PathVariable String uuid,
            @PathVariable Long imageId,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        restaurantImageService.setPrimary(uuid, imageId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> removeImage(
            @PathVariable String uuid,
            @PathVariable Long imageId,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        restaurantImageService.removeImage(uuid, imageId, currentUser);
        return ResponseEntity.noContent().build();
    }

}
