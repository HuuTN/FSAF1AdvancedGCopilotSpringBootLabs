package com.example.model.dto;

import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for creating and updating reviews.
 * Includes validation constraints for input data.
 */
public class ReviewRequestDTO {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Long productId;

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @Size(max = 1000, message = "Review content must not exceed 1000 characters")
    private String reviewContent;

    // Default constructor
    public ReviewRequestDTO() {
    }

    // Constructor with all fields
    public ReviewRequestDTO(Long productId, Long customerId, Integer rating, String reviewContent) {
        this.productId = productId;
        this.customerId = customerId;
        this.rating = rating;
        this.reviewContent = reviewContent;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    @Override
    public String toString() {
        return "ReviewRequestDTO{" +
                "productId=" + productId +
                ", customerId=" + customerId +
                ", rating=" + rating +
                ", reviewContent='" + reviewContent + '\'' +
                '}';
    }
}
