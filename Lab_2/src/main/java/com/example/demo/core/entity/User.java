package com.example.demo.core.entity;

import jakarta.persistence.*;
import com.example.demo.core.enums.UserRole;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductReview> reviews = new HashSet<>();
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    // Default constructor for JPA
    public User() {
        this.orders = new HashSet<>();
        this.reviews = new HashSet<>();
        this.role = UserRole.USER;
    }

    // Constructor with required fields
    public User(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }

    public User(Long id, String name, String email, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Set<ProductReview> getReviews() {
        return reviews;
    }

    public void setReviews(Set<ProductReview> reviews) {
        this.reviews = reviews;
    }
}
