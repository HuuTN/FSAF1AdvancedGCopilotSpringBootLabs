package com.example.controller;

import com.example.model.entity.Review;
import com.example.model.dto.ReviewRequestDTO;
import com.example.service.ReviewService;
import com.example.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Review Management", description = "Operations for managing product reviews")
@Validated
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Get all reviews", description = "Retrieve a list of all reviews")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of reviews")
    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get review by ID", description = "Retrieve a review by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review found"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable @Positive Long id) {
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    @Operation(summary = "Get reviews by product ID", description = "Retrieve all reviews for a specific product")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews for the product")
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProductId(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get reviews by customer ID", description = "Retrieve all reviews by a specific customer")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews by the customer")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Review>> getReviewsByCustomerId(@PathVariable Long customerId) {
        List<Review> reviews = reviewService.getReviewsByCustomerId(customerId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get reviews by rating", description = "Retrieve all reviews with a specific rating")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews with the specified rating")
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<Review>> getReviewsByRating(
            @PathVariable @Min(1) @Max(5) Integer rating) {
        List<Review> reviews = reviewService.getReviewsByRating(rating);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Create a new review", description = "Create a new review for a product with proper validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or customer has already reviewed this product"),
            @ApiResponse(responseCode = "404", description = "Product or customer not found")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Review> createReview(@Valid @RequestBody ReviewRequestDTO reviewRequest) {
        try {
            logger.info("Creating review for product {} by customer {}",
                    reviewRequest.getProductId(), reviewRequest.getCustomerId());

            Review createdReview = reviewService.createReview(
                    reviewRequest.getProductId(),
                    reviewRequest.getCustomerId(),
                    reviewRequest.getRating(),
                    reviewRequest.getReviewContent());

            logger.info("Review created successfully with ID: {}", createdReview.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (Exception e) {
            logger.error("Error creating review: {}", e.getMessage(), e);
            throw e; // Let global exception handler manage this
        }
    }

    @Operation(summary = "Update review", description = "Update an existing review with validation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable @Positive Long id,
            @RequestParam @Min(1) @Max(5) Integer rating,
            @RequestParam(required = false) String reviewContent) {
        try {
            logger.info("Updating review with ID: {}", id);
            Review updatedReview = reviewService.updateReview(id, rating, reviewContent);
            logger.info("Review updated successfully");
            return ResponseEntity.ok(updatedReview);
        } catch (Exception e) {
            logger.error("Error updating review: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(summary = "Delete review", description = "Delete a review by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get average rating for product", description = "Get the average rating for a specific product")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved average rating")
    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByProductId(@PathVariable Long productId) {
        Double averageRating = reviewService.getAverageRatingByProductId(productId);
        return ResponseEntity.ok(averageRating != null ? averageRating : 0.0);
    }

    @Operation(summary = "Get review count for product", description = "Get the total number of reviews for a specific product")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved review count")
    @GetMapping("/product/{productId}/count")
    public ResponseEntity<Long> getReviewCountByProductId(@PathVariable Long productId) {
        Long reviewCount = reviewService.getReviewCountByProductId(productId);
        return ResponseEntity.ok(reviewCount);
    }

    @Operation(summary = "Get reviews by product (sorted by date)", description = "Retrieve reviews for a product sorted by creation date (newest first)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved sorted reviews")
    @GetMapping("/product/{productId}/sorted")
    public ResponseEntity<List<Review>> getReviewsByProductIdSortedByDate(@PathVariable Long productId) {
        List<Review> reviews = reviewService.getReviewsByProductIdSortedByDate(productId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get reviews by customer (sorted by date)", description = "Retrieve reviews by a customer sorted by creation date (newest first)")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved sorted reviews")
    @GetMapping("/customer/{customerId}/sorted")
    public ResponseEntity<List<Review>> getReviewsByCustomerIdSortedByDate(@PathVariable Long customerId) {
        List<Review> reviews = reviewService.getReviewsByCustomerIdSortedByDate(customerId);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Check if customer has reviewed product", description = "Check if a customer has already reviewed a specific product")
    @ApiResponse(responseCode = "200", description = "Successfully checked review status")
    @GetMapping("/customer/{customerId}/product/{productId}/exists")
    public ResponseEntity<Boolean> hasCustomerReviewedProduct(@PathVariable Long customerId,
            @PathVariable Long productId) {
        boolean hasReviewed = reviewService.hasCustomerReviewedProduct(customerId, productId);
        return ResponseEntity.ok(hasReviewed);
    }
}
