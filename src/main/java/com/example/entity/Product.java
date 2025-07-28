package com.example.entity;

import com.example.entity.base.Auditable;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Product extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Integer stock;
    
    private String imageUrl;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(precision = 3, scale = 2)
    private Double averageRating = 0.0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Review> reviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private final List<OrderItem> orderItems = new ArrayList<>();
    
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
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public void addReview(Review review) {
        reviews.add(review);
        review.setProduct(this);
    }
    
    public void removeReview(Review review) {
        reviews.remove(review);
        review.setProduct(null);
    }
    
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }
    
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setProduct(this);
    }
    
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setProduct(null);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        if (!super.equals(o)) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id);
    }
}
