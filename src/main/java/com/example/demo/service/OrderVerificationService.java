package com.example.demo.service;

public interface OrderVerificationService {
    /**
     * Verify if a user has purchased a specific product
     * 
     * @param userId ID of the user to check
     * @param productId ID of the product to verify
     * @return true if the user has purchased the product, false otherwise
     */
    boolean hasUserPurchasedProduct(Long userId, Long productId);
}
