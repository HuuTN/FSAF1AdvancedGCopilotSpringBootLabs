package com.example.service;

import com.example.dto.ReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO);
    Page<ReviewDTO> getReviewsByProduct(Long productId, Pageable pageable);
    Page<ReviewDTO> getReviewsByUser(Long userId, Pageable pageable);
    Double getAverageRatingForProduct(Long productId);
    void deleteReview(Long reviewId, Long userId);
}
