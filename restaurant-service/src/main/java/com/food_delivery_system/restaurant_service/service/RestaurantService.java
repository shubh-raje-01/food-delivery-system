package com.food_delivery_system.restaurant_service.service;

import com.food_delivery_system.restaurant_service.dto.request.CreateRestaurantRequest;
import com.food_delivery_system.restaurant_service.dto.request.UpdateRestaurantRequest;
import com.food_delivery_system.restaurant_service.dto.response.RestaurantResponse;
import com.food_delivery_system.restaurant_service.enums.Role;
import com.food_delivery_system.restaurant_service.exception.ResourceNotFoundException;
import com.food_delivery_system.restaurant_service.exception.UnauthorizedActionException;
import com.food_delivery_system.restaurant_service.mapper.RestaurantMapper;
import com.food_delivery_system.restaurant_service.model.Restaurant;
import com.food_delivery_system.restaurant_service.repository.OpeningHoursRepository;
import com.food_delivery_system.restaurant_service.repository.RestaurantImageRepository;
import com.food_delivery_system.restaurant_service.repository.RestaurantRepository;
import com.food_delivery_system.restaurant_service.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantImageRepository restaurantImageRepository;
    private final OpeningHoursRepository openingHoursRepository;
    private final CategoryService categoryService;
    private final RestaurantMapper restaurantMapper;

    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Transactional
    public RestaurantResponse create(CreateRestaurantRequest request, AuthenticatedUser currentUser) {
        Restaurant restaurant = Restaurant.builder()
                .ownerUuid(currentUser.userId())
                .name(request.getName())
                .description(request.getDescription())
                .address(restaurantMapper.toAddress(request.getAddress()))
                .phone(request.getPhone())
                .email(request.getEmail())
                .categories(categoryService.findOrCreateAll(request.getCategories()))
                .build();

        restaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant created: uuid={}, ownerUuid={}", restaurant.getUuid(), restaurant.getOwnerUuid());

        return restaurantMapper.toResponse(restaurant, List.of(), List.of());
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getByUuid(String uuid) {
        Restaurant restaurant = findByUuidOrThrow(uuid);
        return toFullResponse(restaurant);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantResponse> list(Pageable pageable) {
        return restaurantRepository.findAll(pageable).map(this::toFullResponse);
    }

    @PreAuthorize("hasAnyRole('RESTAURANT_OWNER', 'ADMIN')")
    @Transactional
    public RestaurantResponse update(String uuid, UpdateRestaurantRequest request, AuthenticatedUser currentUser) {
        Restaurant restaurant = findByUuidOrThrow(uuid);
        assertOwnerOrAdmin(restaurant, currentUser);

        restaurant.setName(request.getName());
        restaurant.setDescription(request.getDescription());
        restaurant.setAddress(restaurantMapper.toAddress(request.getAddress()));
        restaurant.setPhone(request.getPhone());
        restaurant.setEmail(request.getEmail());
        restaurant.setCategories(categoryService.findOrCreateAll(request.getCategories()));

        restaurant = restaurantRepository.save(restaurant);
        log.info("Restaurant updated: uuid={}", restaurant.getUuid());

        return toFullResponse(restaurant);
    }

    private RestaurantResponse toFullResponse(Restaurant restaurant) {
        var images = restaurantImageRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurant.getId());
        var hours = openingHoursRepository.findByRestaurantId(restaurant.getId());
        return restaurantMapper.toResponse(
                restaurant,
                images.stream().toList(),
                hours.stream().toList()
        );
    }

    private Restaurant findByUuidOrThrow(String uuid) {
        return restaurantRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found: " + uuid));
    }

    private void assertOwnerOrAdmin(Restaurant restaurant, AuthenticatedUser currentUser) {
        boolean isOwner = restaurant.getOwnerUuid().equals(currentUser.userId());
        boolean isAdmin = currentUser.role() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new UnauthorizedActionException("You do not own this restaurant");
        }
    }

}
