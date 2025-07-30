package com.example.service;

import com.example.model.entity.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    
    List<Review> getAllReviews();
    
    Optional<Review> getReviewById(Long id);
    
    List<Review> getReviewsByProductId(Long productId);
    
    List<Review> getReviewsByCustomerId(Long customerId);
    
    List<Review> getReviewsByRating(Integer rating);
    
    Review createReview(Long productId, Long customerId, Integer rating, String reviewContent);
    
    Review updateReview(Long id, Integer rating, String reviewContent);
    
    void deleteReview(Long id);
    
    Double getAverageRatingByProductId(Long productId);
    
    Long getReviewCountByProductId(Long productId);
    
    List<Review> getReviewsByProductIdSortedByDate(Long productId);
    
    List<Review> getReviewsByCustomerIdSortedByDate(Long customerId);
    
    boolean hasCustomerReviewedProduct(Long customerId, Long productId);
}
