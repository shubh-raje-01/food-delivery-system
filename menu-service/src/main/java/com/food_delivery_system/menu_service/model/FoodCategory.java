package com.food_delivery_system.menu_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "food_categories", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"restaurant_uuid", "name"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_uuid", nullable = false)
    private String restaurantUuid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private int displayOrder = 0;

}