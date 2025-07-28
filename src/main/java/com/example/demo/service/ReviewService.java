package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.ReviewDTO;

public interface ReviewService {
  ReviewDTO addReview(ReviewDTO reviewDTO, Long userId, Long productId);
  List<ReviewDTO> getReviewsByProductId(Long productId);
  List<ReviewDTO> getReviewsByUserId(Long userId);
}
