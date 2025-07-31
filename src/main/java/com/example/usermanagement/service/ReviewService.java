package com.example.usermanagement.service;

import com.example.usermanagement.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {
    ReviewDTO addReview(Long userId, Long productId, int rating, String comment);
    List<ReviewDTO> getReviewsByProduct(Long productId);
    Double getAverageRating(Long productId);
} 