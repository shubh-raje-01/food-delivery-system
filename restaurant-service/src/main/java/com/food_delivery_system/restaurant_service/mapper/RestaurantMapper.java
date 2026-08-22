package com.food_delivery_system.restaurant_service.mapper;

import com.food_delivery_system.restaurant_service.dto.request.AddressRequest;
import com.food_delivery_system.restaurant_service.dto.response.AddressResponse;
import com.food_delivery_system.restaurant_service.dto.response.OpeningHoursResponse;
import com.food_delivery_system.restaurant_service.dto.response.RestaurantImageResponse;
import com.food_delivery_system.restaurant_service.dto.response.RestaurantResponse;
import com.food_delivery_system.restaurant_service.model.Address;
import com.food_delivery_system.restaurant_service.model.Category;
import com.food_delivery_system.restaurant_service.model.OpeningHours;
import com.food_delivery_system.restaurant_service.model.Restaurant;
import com.food_delivery_system.restaurant_service.model.RestaurantImage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RestaurantMapper {

    public Address toAddress(AddressRequest request) {
        return new Address(
                request.getLine1(),
                request.getLine2(),
                request.getCity(),
                request.getState(),
                request.getPostalCode(),
                request.getCountry(),
                request.getLatitude(),
                request.getLongitude()
        );
    }

    public RestaurantResponse toResponse(Restaurant restaurant, List<RestaurantImage> images, List<OpeningHours> openingHours) {
        return RestaurantResponse.builder()
                .uuid(restaurant.getUuid())
                .ownerUuid(restaurant.getOwnerUuid())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .address(toAddressResponse(restaurant.getAddress()))
                .phone(restaurant.getPhone())
                .email(restaurant.getEmail())
                .status(restaurant.getStatus())
                .open(restaurant.isOpen())
                .rating(restaurant.getRating())
                .categories(toCategoryNames(restaurant.getCategories()))
                .images(toImageResponses(images))
                .openingHours(toOpeningHoursResponses(openingHours))
                .createdAt(restaurant.getCreatedAt())
                .build();
    }

    private AddressResponse toAddressResponse(Address address) {
        if (address == null) return null;
        return AddressResponse.builder()
                .line1(address.getLine1())
                .line2(address.getLine2())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    private Set<String> toCategoryNames(Set<Category> categories) {
        if (categories == null) return Set.of();
        return categories.stream().map(Category::getName).collect(Collectors.toSet());
    }

    private List<RestaurantImageResponse> toImageResponses(List<RestaurantImage> images) {
        if (images == null) return List.of();
        return images.stream()
                .map(img -> RestaurantImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .primary(img.isPrimary())
                        .displayOrder(img.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
    }

    private List<OpeningHoursResponse> toOpeningHoursResponses(List<OpeningHours> hours) {
        if (hours == null) return List.of();
        return hours.stream()
                .map(h -> OpeningHoursResponse.builder()
                        .dayOfWeek(h.getDayOfWeek())
                        .openTime(h.getOpenTime())
                        .closeTime(h.getCloseTime())
                        .closed(h.isClosed())
                        .build())
                .collect(Collectors.toList());
    }

}