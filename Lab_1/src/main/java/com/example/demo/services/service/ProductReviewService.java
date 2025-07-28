package com.example.demo.services.service;

import com.example.demo.cores.dtos.CreateProductReviewDTO;
import com.example.demo.cores.dtos.ProductReviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductReviewService {
    
    ProductReviewDTO createReview(Long userId, CreateProductReviewDTO createReviewDTO);
    
    Page<ProductReviewDTO> getProductReviews(Long productId, Pageable pageable);
    
    Page<ProductReviewDTO> getUserReviews(Long userId, Pageable pageable);
    
    Page<ProductReviewDTO> getProductReviewsByRating(Long productId, Integer rating, Pageable pageable);
    
    Page<ProductReviewDTO> getVerifiedProductReviews(Long productId, Pageable pageable);
    
    Double getProductAverageRating(Long productId);
    
    Long getProductTotalReviews(Long productId);
}
