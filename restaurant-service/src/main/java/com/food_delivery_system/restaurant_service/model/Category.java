package com.food_delivery_system.restaurant_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "North Indian", "Fast Food", "Desserts", "Italian", "Maharashtrian", "South Indian"

    @ManyToMany(mappedBy = "categories")
    private Set<Restaurant> restaurants = new HashSet<>();

    public Category(String name) {
        this.name = name;
    }

}
