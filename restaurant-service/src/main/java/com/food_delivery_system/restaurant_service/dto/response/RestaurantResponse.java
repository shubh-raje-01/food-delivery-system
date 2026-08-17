package com.food_delivery_system.restaurant_service.dto.response;

import com.food_delivery_system.restaurant_service.enums.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {

    private String uuid;
    private String ownerUuid;
    private String name;
    private String description;
    private AddressResponse address;
    private String phone;
    private String email;
    private RestaurantStatus status;
    private boolean open;
    private Double rating;
    private Set<String> categories;
    private List<RestaurantImageResponse> images;
    private List<OpeningHoursResponse> openingHours;
    private Instant createdAt;

}
