package com.food_delivery_system.menu_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "menu_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = "uuid")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, updatable = false)
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();

    @Column(name = "restaurant_uuid", nullable = false)
    private String restaurantUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_category_id", nullable = false)
    private FoodCategory category;

    @Column(nullable = false)
    private String name;

    @Column(length = 100)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(nullable = false)
    @Builder.Default
    private boolean available = true;

    @Column(nullable = false)
    @Builder.Default
    private boolean vegetarian = false;

    private String imageUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

}
