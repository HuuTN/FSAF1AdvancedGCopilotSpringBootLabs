package com.example.demo.apis.controller;

import com.example.demo.core.dtos.ProductReviewDTO;
import com.example.demo.services.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
public class ProductReviewController {
    
    @Autowired
    private ProductReviewService reviewService;

    @Operation(summary = "Create a product review", description = "User creates a review for a purchased product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or user cannot review this product")
    })
    @PostMapping
    public ResponseEntity<?> createReview(@Valid @RequestBody ProductReviewDTO reviewDTO) {
        try {
            ProductReviewDTO createdReview = reviewService.createReview(reviewDTO);
            return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update a review", description = "User updates their own review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Review not found"),
        @ApiResponse(responseCode = "403", description = "User can only update their own reviews")
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, 
                                        @Valid @RequestBody ProductReviewDTO reviewDTO,
                                        @RequestParam Long userId) {
        try {
            ProductReviewDTO updatedReview = reviewService.updateReview(reviewId, reviewDTO, userId);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Delete a review", description = "User deletes their own review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Review not found"),
        @ApiResponse(responseCode = "403", description = "User can only delete their own reviews")
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId, @RequestParam Long userId) {
        try {
            reviewService.deleteReview(reviewId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", e.getMessage());
            error.put("status", HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get reviews for a product", description = "Returns paginated reviews for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ProductReviewDTO>> getReviewsByProduct(@PathVariable Long productId, 
                                                                     Pageable pageable) {
        Page<ProductReviewDTO> reviews = reviewService.getReviewsByProduct(productId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get reviews by user", description = "Returns paginated reviews by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProductReviewDTO>> getReviewsByUser(@PathVariable Long userId, 
                                                                  Pageable pageable) {
        Page<ProductReviewDTO> reviews = reviewService.getReviewsByUser(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get user's review for a product", description = "Returns user's review for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review found"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<ProductReviewDTO> getUserReviewForProduct(@PathVariable Long userId, 
                                                                   @PathVariable Long productId) {
        ProductReviewDTO review = reviewService.getUserReviewForProduct(userId, productId);
        if (review != null) {
            return ResponseEntity.ok(review);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Check if user can review product", description = "Checks if user can review a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed")
    })
    @GetMapping("/can-review")
    public ResponseEntity<Map<String, Boolean>> canUserReviewProduct(@RequestParam Long userId, 
                                                                    @RequestParam Long productId) {
        boolean canReview = reviewService.canUserReviewProduct(userId, productId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("canReview", canReview);
        return ResponseEntity.ok(response);
    }
}
