package com.example.demo.entity;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "product", indexes = {
    @Index(name = "idx_product_name_lower", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category_id")
})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    private String description;
    private Double price;
    private Integer stockQuantity;
    
    @Column(nullable = false)
    private Double averageRating = 0.0;
    
    @Column(nullable = false)
    private Integer totalReviews = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

    public Product() {
    }

    public Product(Long id, String name, String description, Double price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // getters and setters
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

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
}
