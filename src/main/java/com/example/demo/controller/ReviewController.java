package com.example.demo.controller;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.service.ReviewService;
import com.example.demo.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    /**
     * Create a new review for a product
     * Requires authentication and validates that the user has purchased the product
     * 
     * @param productId ID of the product to review
     * @param reviewDTO Review data including rating and comment
     * @param userId ID of the authenticated user (injected by security context)
     * @return The created review
     */
    @PostMapping("/products/{productId}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewDTO reviewDTO
            ) {

        // Lấy username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Tra cứu userId từ username
        Long userId = userService.getUserIdByUsername(username); // Bạn cần implement hàm này
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        ReviewDTO createdReview = reviewService.addReview(reviewDTO, userId, productId);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    /**
     * Get all reviews for a product
     * Public endpoint, no authentication required
     * 
     * @param productId ID of the product to get reviews for
     * @return List of reviews for the product
     */
    @GetMapping("/products/{productId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getProductReviews(
            @PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * Get all reviews by a user
     * Requires authentication and can only access own reviews
     * 
     * @param userId ID of the user to get reviews for
     * @return List of reviews by the user
     */
    @GetMapping("/users/{userId}/reviews")
    @PreAuthorize("authentication.principal.id == #userId")
    public ResponseEntity<List<ReviewDTO>> getUserReviews(
            @PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }
}
