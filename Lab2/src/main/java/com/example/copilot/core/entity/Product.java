package com.example.copilot.core.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "product", indexes = {
    // Single column indexes for frequent queries
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_price", columnList = "price"), 
    @Index(name = "idx_product_category", columnList = "category_id"),
    @Index(name = "idx_product_stock", columnList = "stock_quantity"),
    @Index(name = "idx_product_rating", columnList = "average_rating"),
    
    // Composite indexes for frequent query combinations
    @Index(name = "idx_product_category_price", columnList = "category_id, price"),
    @Index(name = "idx_product_name_category", columnList = "name, category_id"),
    @Index(name = "idx_product_price_stock", columnList = "price, stock_quantity"),
    @Index(name = "idx_product_category_rating", columnList = "category_id, average_rating")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @DecimalMin("0.0")
    private Double price;

    @NotNull
    @Min(0)
    private Integer stockQuantity;

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private java.util.Set<OrderItem> orderItems;

    // Getters and Setters
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    public Double getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    public Integer getReviewCount() {
        return reviewCount;
    }
    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public java.util.Set<OrderItem> getOrderItems() {
        return orderItems;
    }
    public void setOrderItems(java.util.Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
