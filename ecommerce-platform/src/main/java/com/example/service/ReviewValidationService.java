package com.example.service;

import com.example.model.entity.Customer;
import com.example.model.entity.Product;

/**
 * Service interface for review validation operations.
 * Provides methods to validate review eligibility, rating values, and content.
 */
public interface ReviewValidationService {

    /**
     * Validates if a customer is eligible to review a product.
     * Customer must have purchased the product and not already reviewed it.
     * 
     * @param customer the customer attempting to review
     * @param product  the product being reviewed
     * @throws UserNotPurchasedProductException if customer hasn't purchased the
     *                                          product
     * @throws DuplicateReviewException         if customer has already reviewed the
     *                                          product
     */
    void validateReviewEligibility(Customer customer, Product product);

    /**
     * Validates the rating value is within acceptable range (1-5).
     * 
     * @param rating the rating value to validate
     * @throws IllegalArgumentException if rating is not between 1 and 5
     */
    void validateRating(Integer rating);

    /**
     * Validates review content for XSS and length.
     * 
     * @param content the review content to validate
     * @throws IllegalArgumentException if content exceeds maximum length
     */
    void validateReviewContent(String content);
}