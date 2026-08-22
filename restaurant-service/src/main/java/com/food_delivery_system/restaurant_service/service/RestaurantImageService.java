package com.food_delivery_system.restaurant_service.service;

import com.food_delivery_system.restaurant_service.dto.request.RestaurantImageRequest;
import com.food_delivery_system.restaurant_service.exception.ResourceNotFoundException;
import com.food_delivery_system.restaurant_service.model.Restaurant;
import com.food_delivery_system.restaurant_service.model.RestaurantImage;
import com.food_delivery_system.restaurant_service.repository.RestaurantImageRepository;
import com.food_delivery_system.restaurant_service.repository.RestaurantRepository;
import com.food_delivery_system.restaurant_service.security.AuthenticatedUser;
import com.food_delivery_system.restaurant_service.security.RestaurantOwnershipGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantImageService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantImageRepository restaurantImageRepository;
    private final RestaurantOwnershipGuard ownershipGuard;

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @Transactional
    public void addImage(String restaurantUuid, RestaurantImageRequest request, AuthenticatedUser currentUser) {
        Restaurant restaurant = findRestaurantOrThrow(restaurantUuid);
        ownershipGuard.assertOwnerOrAdmin(restaurant, currentUser);

        if (request.isPrimary()) {
            clearExistingPrimary(restaurant.getId());
        }

        RestaurantImage image = RestaurantImage.builder()
                .restaurant(restaurant)
                .imageUrl(request.getImageUrl())
                .primary(request.isPrimary())
                .displayOrder(request.getDisplayOrder())
                .build();

        restaurantImageRepository.save(image);
    }

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @Transactional
    public void setPrimary(String restaurantUuid, Long imageId, AuthenticatedUser currentUser) {
        Restaurant restaurant = findRestaurantOrThrow(restaurantUuid);
        ownershipGuard.assertOwnerOrAdmin(restaurant, currentUser);

        RestaurantImage target = restaurantImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + imageId));

        clearExistingPrimary(restaurant.getId());
        target.setPrimary(true);
        restaurantImageRepository.save(target);
    }

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @Transactional
    public void removeImage(String restaurantUuid, Long imageId, AuthenticatedUser currentUser) {
        Restaurant restaurant = findRestaurantOrThrow(restaurantUuid);
        ownershipGuard.assertOwnerOrAdmin(restaurant, currentUser);

        RestaurantImage image = restaurantImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + imageId));

        restaurantImageRepository.delete(image);
    }

    private void clearExistingPrimary(Long restaurantId) {
        List<RestaurantImage> images = restaurantImageRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId);
        images.stream()
                .filter(RestaurantImage::isPrimary)
                .forEach(img -> img.setPrimary(false));
        restaurantImageRepository.saveAll(images);
    }

    private Restaurant findRestaurantOrThrow(String restaurantUuid) {
        return restaurantRepository.findByUuid(restaurantUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + restaurantUuid));
    }

}
