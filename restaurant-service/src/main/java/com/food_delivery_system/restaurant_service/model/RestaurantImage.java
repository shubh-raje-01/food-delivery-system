package com.food_delivery_system.restaurant_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean primary = false;

    @Column(nullable = false)
    @Builder.Default
    private int displayOrder = 0;

}
