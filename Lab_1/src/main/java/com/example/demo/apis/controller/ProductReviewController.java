package com.example.demo.apis.controller;

import com.example.demo.cores.dtos.CreateProductReviewDTO;
import com.example.demo.cores.dtos.ProductReviewDTO;
import com.example.demo.services.service.ProductReviewService;
import com.example.demo.services.service.UserSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@Tag(name = "Product Reviews", description = "APIs for managing product reviews and ratings")
public class ProductReviewController {
    
    @Autowired
    private ProductReviewService productReviewService;
    
    @Autowired
    private UserSecurityService userSecurityService;
    
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @Operation(
        summary = "Create Product Review", 
        description = "Create a new product review. User must be authenticated and have purchased the product to review it."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Review created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or user hasn't purchased the product"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "User not authorized"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "409", description = "User has already reviewed this product"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductReviewDTO> createReview(
            Authentication authentication,
            
            @Parameter(description = "Review details", required = true)
            @RequestBody @Valid CreateProductReviewDTO createReviewDTO) {
        
        // Get current user ID from authentication instead of trusting user input
        Long userId = userSecurityService.getCurrentUserId(authentication);
        
        ProductReviewDTO review = productReviewService.createReview(userId, createReviewDTO);
        return new ResponseEntity<>(review, HttpStatus.CREATED);
    }
    
    @GetMapping("/product/{productId}")
    @Operation(
        summary = "Get Product Reviews", 
        description = "Get all reviews for a specific product with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProductReviewDTO>> getProductReviews(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable @NotNull Long productId,
            
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        
        Page<ProductReviewDTO> reviews = productReviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(
        summary = "Get User Reviews", 
        description = "Get all reviews created by a specific user with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProductReviewDTO>> getUserReviews(
            @Parameter(description = "User ID", required = true, example = "1")
            @PathVariable @NotNull Long userId,
            
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        
        Page<ProductReviewDTO> reviews = productReviewService.getUserReviews(userId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/product/{productId}/rating/{rating}")
    @Operation(
        summary = "Get Product Reviews by Rating", 
        description = "Get reviews for a product filtered by specific rating"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid rating value"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProductReviewDTO>> getProductReviewsByRating(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable @NotNull Long productId,
            
            @Parameter(description = "Rating (1-5)", required = true, example = "5")
            @PathVariable @NotNull Integer rating,
            
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        
        Page<ProductReviewDTO> reviews = productReviewService.getProductReviewsByRating(productId, rating, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/product/{productId}/verified")
    @Operation(
        summary = "Get Verified Product Reviews", 
        description = "Get only verified purchase reviews for a product"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<ProductReviewDTO>> getVerifiedProductReviews(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable @NotNull Long productId,
            
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        
        Page<ProductReviewDTO> reviews = productReviewService.getVerifiedProductReviews(productId, pageable);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/product/{productId}/average-rating")
    @Operation(
        summary = "Get Product Average Rating", 
        description = "Get the average rating for a specific product"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Average rating retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> getProductAverageRating(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable @NotNull Long productId) {
        
        Double averageRating = productReviewService.getProductAverageRating(productId);
        return ResponseEntity.ok(averageRating);
    }
    
    @GetMapping("/product/{productId}/total-reviews")
    @Operation(
        summary = "Get Product Total Reviews Count", 
        description = "Get the total number of reviews for a specific product"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total reviews count retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Long> getProductTotalReviews(
            @Parameter(description = "Product ID", required = true, example = "1")
            @PathVariable @NotNull Long productId) {
        
        Long totalReviews = productReviewService.getProductTotalReviews(productId);
        return ResponseEntity.ok(totalReviews);
    }
}
