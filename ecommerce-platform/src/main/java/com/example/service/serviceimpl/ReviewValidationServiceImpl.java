package com.example.service.serviceimpl;

import com.example.model.entity.Customer;
import com.example.model.entity.Product;
import com.example.repository.OrderRepository;
import com.example.repository.ReviewRepository;
import com.example.service.ReviewValidationService;
import com.example.exception.UserNotPurchasedProductException;
import com.example.exception.DuplicateReviewException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of ReviewValidationService.
 * Handles business rules and validation logic for reviews.
 */
@Service
public class ReviewValidationServiceImpl implements ReviewValidationService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewValidationServiceImpl.class);

    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewValidationServiceImpl(OrderRepository orderRepository,
            ReviewRepository reviewRepository) {
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
    }

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
    @Override
    public void validateReviewEligibility(Customer customer, Product product) {
        logger.debug("Validating review eligibility for customer {} and product {}", customer.getId(), product.getId());

        // Check if customer has purchased the product (delivered orders only)
        boolean hasPurchased = orderRepository.hasCustomerPurchasedProduct(customer.getId(), product.getId());
        if (!hasPurchased) {
            throw new UserNotPurchasedProductException(
                    "Customer has not purchased this product or the order is not yet delivered");
        }

        // Check if customer has already reviewed this product
        boolean hasReviewed = reviewRepository.existsByCustomer_IdAndProduct_Id(customer.getId(), product.getId());
        if (hasReviewed) {
            throw new DuplicateReviewException("Customer has already reviewed this product");
        }

        logger.debug("Review eligibility validation passed for customer {} and product {}", customer.getId(),
                product.getId());
    }

    /**
     * Validates rating value.
     * 
     * @param rating the rating to validate
     * @throws IllegalArgumentException if rating is not between 1 and 5
     */
    @Override
    public void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    /**
     * Validates review content for XSS and length.
     * 
     * @param content the review content to validate
     * @throws IllegalArgumentException if content exceeds maximum length
     */
    @Override
    public void validateReviewContent(String content) {
        if (content != null) {
            if (content.length() > 1000) {
                throw new IllegalArgumentException("Review content must not exceed 1000 characters");
            }

            // Basic XSS prevention - remove potentially harmful tags
            String sanitized = sanitizeHtml(content);
            if (!sanitized.equals(content)) {
                logger.warn("Potentially harmful content detected and sanitized in review");
            }
        }
    }

    /**
     * Basic HTML sanitization to prevent XSS attacks.
     * 
     * @param content the content to sanitize
     * @return sanitized content
     */
    private String sanitizeHtml(String content) {
        if (content == null)
            return null;

        // Remove potentially harmful HTML tags and scripts
        return content.replaceAll("<script[^>]*>.*?</script>", "")
                .replaceAll("<iframe[^>]*>.*?</iframe>", "")
                .replaceAll("<object[^>]*>.*?</object>", "")
                .replaceAll("<embed[^>]*>.*?</embed>", "")
                .replaceAll("javascript:", "")
                .replaceAll("vbscript:", "")
                .replaceAll("onload=", "")
                .replaceAll("onerror=", "")
                .replaceAll("onclick=", "");
    }
}
