package com.food_delivery_system.restaurant_service.service;

import com.food_delivery_system.restaurant_service.dto.request.OpeningHoursRequest;
import com.food_delivery_system.restaurant_service.exception.InvalidRestaurantStateException;
import com.food_delivery_system.restaurant_service.exception.ResourceNotFoundException;
import com.food_delivery_system.restaurant_service.model.OpeningHours;
import com.food_delivery_system.restaurant_service.model.Restaurant;
import com.food_delivery_system.restaurant_service.repository.OpeningHoursRepository;
import com.food_delivery_system.restaurant_service.repository.RestaurantRepository;
import com.food_delivery_system.restaurant_service.security.AuthenticatedUser;
import com.food_delivery_system.restaurant_service.security.RestaurantOwnershipGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OpeningHoursService {

    private final RestaurantRepository restaurantRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final RestaurantOwnershipGuard ownershipGuard;

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @Transactional
    public void setHours(String restaurantUuid, List<OpeningHoursRequest> requests, AuthenticatedUser currentUser) {
        Restaurant restaurant = restaurantRepository.findByUuid(restaurantUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantUuid));
        ownershipGuard.assertOwnerOrAdmin(restaurant, currentUser);

        validateNoDuplicateDays(requests);
        requests.forEach(this::validateTimes);

        openingHoursRepository.deleteByRestaurantId(restaurant.getId());

        List<OpeningHours> entities = requests.stream()
                .map(req -> OpeningHours.builder()
                        .restaurant(restaurant)
                        .dayOfWeek(req.getDayOfWeek())
                        .openTime(req.isClosed() ? null : req.getOpenTime())
                        .closeTime(req.isClosed() ? null : req.getCloseTime())
                        .closed(req.isClosed())
                        .build())
                .collect(Collectors.toList());

        openingHoursRepository.saveAll(entities);
    }

    private void validateNoDuplicateDays(List<OpeningHoursRequest> requests) {
        Set<DayOfWeek> seen = new HashSet<>();
        for (OpeningHoursRequest req : requests) {
            if (!seen.add(req.getDayOfWeek())) {
                throw new InvalidRestaurantStateException(
                        "Duplicate entry for " + req.getDayOfWeek() + " — only one schedule per day is allowed");
            }
        }
    }

    private void validateTimes(OpeningHoursRequest req) {
        if (req.isClosed()) {
            return;
        }
        if (req.getOpenTime() == null || req.getCloseTime() == null) {
            throw new InvalidRestaurantStateException(
                    req.getDayOfWeek() + ": openTime and closeTime are required unless closed=true");
        }
        if (!req.getCloseTime().isAfter(req.getOpenTime())) {
            throw new InvalidRestaurantStateException(
                    req.getDayOfWeek() + ": closeTime must be after openTime");
        }
    }

}