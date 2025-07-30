package com.example.copilot.service;

import java.util.List;

import com.example.copilot.dto.ReviewDTO;

public interface ReviewService {
    ReviewDTO addReview(Long userId, Long productId, int rating, String comment);
    List<ReviewDTO> getReviewsByProduct(Long productId);
    Double getAverageRating(Long productId);
}
