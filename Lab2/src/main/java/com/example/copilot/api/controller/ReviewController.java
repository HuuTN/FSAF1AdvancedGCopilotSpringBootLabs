package com.example.copilot.api.controller;

import com.example.copilot.core.dto.ReviewDTO;
import com.example.copilot.service.ReviewService;
import com.example.copilot.service.OrderVerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing product reviews
 * SECURITY: CORS configured globally in SecurityConfig for better control
 */
@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private OrderVerificationService orderVerificationService;

    /**
     * Add a new review for a product (Secured Endpoint)
     * POST /products/{productId}/reviews
     */
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewDTO> addReview(
            @PathVariable Long productId,
            @RequestParam Long userId, // Temporary - should be from authentication
            @Valid @RequestBody ReviewDTO reviewDTO) {
        
        // Set user and product IDs
        reviewDTO.setUserId(userId);
        reviewDTO.setProductId(productId);
        
        ReviewDTO savedReview = reviewService.addReview(reviewDTO);
        
        return new ResponseEntity<>(savedReview, HttpStatus.CREATED);
    }

    /**
     * Get all reviews for a specific product (Public Endpoint)
     * GET /products/{productId}/reviews
     */
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getProductReviews(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get all reviews by a specific user
     * GET /users/{userId}/reviews
     */
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getUserReviews(@PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Delete a review
     * DELETE /reviews/{reviewId}
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) { // Temporary - should be from authentication
        
        reviewService.deleteReview(reviewId, userId);
        
        return ResponseEntity.noContent().build();
    }

    /**
     * Check if user has purchased a product (for review validation)
     * GET /products/{productId}/purchase-check?userId={userId}
     */
    @GetMapping("/products/{productId}/purchase-check")
    public ResponseEntity<Boolean> checkPurchaseStatus(
            @PathVariable Long productId,
            @RequestParam Long userId) { // Temporary - should be from authentication
        
        boolean hasPurchased = orderVerificationService.hasUserPurchasedProduct(userId, productId);
        
        return ResponseEntity.ok(hasPurchased);
    }
}
