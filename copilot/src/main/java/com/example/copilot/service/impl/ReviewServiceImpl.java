package com.example.copilot.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.copilot.dto.ReviewDTO;
import com.example.copilot.entity.Product;
import com.example.copilot.entity.Review;
import com.example.copilot.exception.DuplicateReviewException;
import com.example.copilot.exception.ProductNotFoundException;
import com.example.copilot.exception.UserNotPurchasedProductException;
import com.example.copilot.repository.OrderRepository;
import com.example.copilot.repository.ProductRepository;
import com.example.copilot.repository.ReviewRepository;
import com.example.copilot.service.ReviewService;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public ReviewDTO addReview(Long userId, Long productId, int rating, String comment) {
        // 1. Validate product existence
        getProductOrThrow(productId);

        // 2. Check if user has at least one completed ('DELIVERED') order containing the product
        boolean purchased = orderRepository.existsByUserIdAndProductIdAndStatus(userId, productId, "DELIVERED");
        if (!purchased) {
            throw new UserNotPurchasedProductException("User has not purchased this product or order not delivered");
        }
        // 3. Check if user already reviewed this product
        if (reviewRepository.findByUserIdAndProductId(userId, productId).isPresent()) {
            throw new DuplicateReviewException("User already reviewed this product");
        }
        // 4. Map DTO to entity and save
        Review review = mapToReviewEntity(userId, productId, rating, comment);
        review = reviewRepository.save(review);

        // 5. Update product average rating using helper
        updateProductAverageRating(productId);

        return mapToReviewDTO(review);
    }

    @Override
    public List<ReviewDTO> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToReviewDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double getAverageRating(Long productId) {
        return reviewRepository.findAverageRatingByProductId(productId);
    }

    private Product getProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
    }

    private Review mapToReviewEntity(Long userId, Long productId, int rating, String comment) {
        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        return review;
    }

    private ReviewDTO mapToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setProductId(review.getProductId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    /**
     * Helper method to update the average rating of a product using a JPQL query.
     */
    private void updateProductAverageRating(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        Product product = productRepository.findById(productId).orElseThrow();
        product.setAverageRating(avg != null ? avg : 0.0);
        productRepository.save(product);
    }
}
