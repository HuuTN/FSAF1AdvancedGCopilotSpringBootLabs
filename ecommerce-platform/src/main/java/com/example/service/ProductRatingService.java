package com.example.service;

import java.util.List;

/**
 * Service interface for managing product ratings.
 * Handles calculation and updating of product average ratings.
 */
public interface ProductRatingService {

    /**
     * Updates the average rating for a product based on all its reviews.
     * 
     * @param productId the product ID to update
     */
    void updateProductAverageRating(Long productId);

    /**
     * Gets the current average rating for a product.
     * 
     * @param productId the product ID
     * @return the average rating, or null if no reviews exist
     */
    Double getProductAverageRating(Long productId);

    /**
     * Gets the review count for a product.
     * 
     * @param productId the product ID
     * @return the number of reviews
     */
    Long getProductReviewCount(Long productId);

    /**
     * Batch update ratings for multiple products.
     * Useful for maintenance operations.
     * 
     * @param productIds list of product IDs to update
     */
    void batchUpdateProductRatings(List<Long> productIds);
}
