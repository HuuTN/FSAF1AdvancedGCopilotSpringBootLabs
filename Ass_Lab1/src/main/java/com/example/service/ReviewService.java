package com.example.service;

import com.example.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO, Long userId, Long productId);
    List<ReviewDTO> getProductReviews(Long productId);
    void updateProductAverageRating(Long productId);
    boolean hasUserPurchasedProduct(Long userId, Long productId);
}
