package com.example.dto;

import java.time.LocalDateTime;

public class ReviewDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long productId;
    private String productName;
    private Integer rating;
    private String comment;
    
    private LocalDateTime createdAt;
    
    // Constructors
    public ReviewDTO() {}
    
    public ReviewDTO(Long userId, Long productId, Integer rating, String comment) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Input validation methods
    public boolean isValidRating() {
        return rating != null && rating >= 1 && rating <= 5;
    }
    
    public boolean isValidComment() {
        return comment == null || comment.length() <= 1000;
    }
    
    public boolean isValidIds() {
        return userId != null && userId > 0 && productId != null && productId > 0;
    }
}
