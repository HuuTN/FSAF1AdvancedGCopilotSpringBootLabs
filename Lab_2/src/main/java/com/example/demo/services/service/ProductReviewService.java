package com.example.demo.services.service;

import com.example.demo.core.dtos.ProductReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductReviewService {
    ProductReviewDTO createReview(ProductReviewDTO reviewDTO);
    ProductReviewDTO updateReview(Long reviewId, ProductReviewDTO reviewDTO, Long userId);
    void deleteReview(Long reviewId, Long userId);
    Page<ProductReviewDTO> getReviewsByProduct(Long productId, Pageable pageable);
    Page<ProductReviewDTO> getReviewsByUser(Long userId, Pageable pageable);
    ProductReviewDTO getUserReviewForProduct(Long userId, Long productId);
    boolean canUserReviewProduct(Long userId, Long productId);
}
