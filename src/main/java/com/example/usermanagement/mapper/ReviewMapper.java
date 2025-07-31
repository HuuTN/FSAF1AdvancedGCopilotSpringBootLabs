package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.ReviewDTO;
import com.example.usermanagement.entity.Review;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.entity.Product;

public class ReviewMapper {
    public static ReviewDTO toDTO(Review review) {
        if (review == null) return null;
        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .productId(review.getProduct().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public static Review toEntity(ReviewDTO dto, User user, Product product) {
        if (dto == null) return null;
        return Review.builder()
                .id(dto.getId())
                .user(user)
                .product(product)
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(dto.getCreatedAt())
                .build();
    }
} 