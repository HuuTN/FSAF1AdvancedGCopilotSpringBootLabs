package com.example.copilot.service;

import com.example.copilot.core.dto.ReviewDTO;

import java.util.List;

/**
 * Service interface for managing product reviews
 */
public interface ReviewService {
    
    /**
     * Add a new review for a product
     * @param reviewDTO the review data
     * @return the saved review
     * @throws UserNotPurchasedProductException if user hasn't purchased the product
     * @throws DuplicateReviewException if user has already reviewed the product
     */
    ReviewDTO addReview(ReviewDTO reviewDTO);
    
    /**
     * Get all reviews for a specific product
     * @param productId the product ID
     * @return list of reviews
     */
    List<ReviewDTO> getProductReviews(Long productId);
    
    /**
     * Get all reviews by a specific user
     * @param userId the user ID
     * @return list of reviews
     */
    List<ReviewDTO> getUserReviews(Long userId);
    
    /**
     * Update product average rating after review changes
     * @param productId the product ID
     */
    void updateProductRating(Long productId);
    
    /**
     * Delete a review
     * @param reviewId the review ID
     * @param userId the user ID (for authorization)
     */
    void deleteReview(Long reviewId, Long userId);
    
    /**
     * Check if user has purchased the product
     * @param userId the user ID
     * @param productId the product ID
     * @return true if purchased, false otherwise
     */
    boolean hasUserPurchasedProduct(Long userId, Long productId);
}
