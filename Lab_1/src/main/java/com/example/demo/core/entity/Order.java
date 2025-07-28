package com.example.demo.core.entity;

import jakarta.persistence.*;
// Removed Lombok annotations, using manual getters and setters
import java.util.Set;
import com.example.demo.core.enums.OrderStatus;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "orders")
// Removed Lombok annotations
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    public Order() {}

    public Order(Long id, Set<OrderItem> orderItems, User user, OrderStatus status) {
        this.id = id;
        this.orderItems = orderItems;
        this.user = user;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
