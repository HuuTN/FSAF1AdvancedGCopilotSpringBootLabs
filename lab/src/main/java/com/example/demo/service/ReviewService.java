package com.example.demo.service;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.entity.Review;
import java.util.List;

public interface ReviewService {
    Review addReview(ReviewDTO dto, Long userId, Long productId);
    List<Review> getReviewsByProduct(Long productId);
}
