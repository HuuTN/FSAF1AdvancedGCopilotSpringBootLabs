package com.example.controller;

import com.example.dto.ReviewDTO;
import com.example.exception.DuplicateReviewException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.ReviewNotFoundException;
import com.example.exception.UserNotPurchasedProductException;
import com.example.exception.UserNotFoundException;
import com.example.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO reviewDTO) {
        try {
            ReviewDTO createdReview = reviewService.createReview(reviewDTO);
            return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (UserNotPurchasedProductException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorResponse("Access Denied", e.getMessage()));
        } catch (DuplicateReviewException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(createErrorResponse("Duplicate Review", e.getMessage()));
        } catch (UserNotFoundException | ProductNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Error", "An unexpected error occurred"));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getReviewsByProduct(
            @PathVariable Long productId, 
            Pageable pageable) {
        try {
            Page<ReviewDTO> reviews = reviewService.getReviewsByProduct(productId, pageable);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Error", "An unexpected error occurred"));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getReviewsByUser(
            @PathVariable Long userId, 
            Pageable pageable) {
        try {
            Page<ReviewDTO> reviews = reviewService.getReviewsByUser(userId, pageable);
            return new ResponseEntity<>(reviews, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Error", "An unexpected error occurred"));
        }
    }

    @GetMapping("/product/{productId}/average-rating")
    public ResponseEntity<?> getAverageRating(@PathVariable Long productId) {
        try {
            Double averageRating = reviewService.getAverageRatingForProduct(productId);
            return new ResponseEntity<>(averageRating, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Error", "An unexpected error occurred"));
        }
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId, 
            @RequestParam Long userId) {
        try {
            reviewService.deleteReview(reviewId, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(createErrorResponse("Validation Error", e.getMessage()));
        } catch (ReviewNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Error", "An unexpected error occurred"));
        }
    }
    
    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }
}
