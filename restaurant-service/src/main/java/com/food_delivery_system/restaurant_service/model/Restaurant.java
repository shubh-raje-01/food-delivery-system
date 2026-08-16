package com.food_delivery_system.restaurant_service.model;

import com.food_delivery_system.restaurant_service.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "restaurants", uniqueConstraints = {
        @UniqueConstraint(columnNames = "uuid")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();

    // Raw reference to auth-service's User.uuid — cannot be a real FK across services/databases.
    // Only ever trusted via the gateway-forwarded X-User-Id header, never client-supplied.
    @Column(nullable = false)
    private String ownerUuid;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Embedded
    private Address address;

    @Column(nullable = false)
    private String phone;

    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RestaurantStatus status = RestaurantStatus.PENDING_APPROVAL;

    @Column(nullable = false)
    @Builder.Default
    private boolean open = true;

    @Column(nullable = false)
    @Builder.Default
    private Double rating = 0.0;

    @ManyToMany
    @JoinTable(
            name = "restaurant_categories",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

}

