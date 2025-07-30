package com.example.demo.service;

import com.example.demo.dto.ReviewDTO;
import com.example.demo.entity.*;
import com.example.demo.exception.DuplicateReviewException;
import com.example.demo.exception.UserNotPurchasedProductException;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Review addReview(ReviewDTO dto, Long userId, Long productId) {
        // 1. Kiểm tra user đã mua và đơn đã DELIVERED
        boolean hasPurchased = orderRepository.findAll().stream()
            .anyMatch(order -> order.getUser() != null && order.getUser().getId().equals(userId)
                && order.getStatus() == OrderStatus.DELIVERED
                && order.getItems().stream().anyMatch(item -> item.getProduct().getId().equals(productId)));
        if (!hasPurchased) {
            throw new UserNotPurchasedProductException("User must have a DELIVERED order for this product");
        }
        // 2. Kiểm tra duplicate review
        boolean exists = reviewRepository.existsByUserIdAndProductId(userId, productId);
        if (exists) {
            throw new DuplicateReviewException("User has already reviewed this product");
        }
        // 3. Lưu review
        Review review = new Review();
        review.setUser(userRepository.findById(userId).orElseThrow());
        review.setProduct(productRepository.findById(productId).orElseThrow());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setReviewDate(LocalDateTime.now());
        review = reviewRepository.save(review);
        // Cập nhật lại rating trung bình cho sản phẩm
        updateProductAverageRating(productId);
        return review;
    }

    @Override
    public List<Review> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

    private void updateProductAverageRating(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        Product product = productRepository.findById(productId).orElseThrow();
        product.setAverageRating(avg != null ? avg : 0.0);
        productRepository.save(product);
    }
}
