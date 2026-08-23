package com.food_delivery_system.restaurant_service.controller;

import com.food_delivery_system.restaurant_service.dto.request.OpeningHoursRequest;
import com.food_delivery_system.restaurant_service.security.AuthenticatedUser;
import com.food_delivery_system.restaurant_service.service.OpeningHoursService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/restaurants/{uuid}/opening-hours")
@RequiredArgsConstructor
public class OpeningHoursController {

    private final OpeningHoursService openingHoursService;

    @PutMapping
    public ResponseEntity<Void> setHours(
            @PathVariable String uuid,
            @Valid @NotEmpty @RequestBody List<@Valid OpeningHoursRequest> requests,
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        openingHoursService.setHours(uuid, requests, currentUser);
        return ResponseEntity.noContent().build();
    }

}
