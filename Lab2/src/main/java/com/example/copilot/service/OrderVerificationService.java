package com.example.copilot.service;

import com.example.copilot.core.enums.OrderStatus;

/**
 * Service interface for order verification operations
 * Extracted from ReviewService to follow Single Responsibility Principle
 */
public interface OrderVerificationService {
    
    /**
     * Verify if a user has purchased a specific product with delivered status
     * @param userId the user ID
     * @param productId the product ID
     * @return true if user has purchased the product and it was delivered
     */
    boolean hasUserPurchasedProduct(Long userId, Long productId);
    
    /**
     * Verify if a user has purchased a specific product with specific status
     * @param userId the user ID
     * @param productId the product ID
     * @param status the order status to check
     * @return true if user has purchased the product with given status
     */
    boolean hasUserPurchasedProductWithStatus(Long userId, Long productId, OrderStatus status);
}
