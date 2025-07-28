package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.ReviewDTO;
import com.example.usermanagement.entity.Order;
import com.example.usermanagement.entity.Product;
import com.example.usermanagement.entity.Review;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.EntityNotFoundException;
import com.example.usermanagement.repository.OrderRepository;
import com.example.usermanagement.repository.ProductRepository;
import com.example.usermanagement.repository.ReviewRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.ReviewService;
import com.example.usermanagement.mapper.ReviewMapper;
import com.example.usermanagement.constant.OrderStatus;
import com.example.usermanagement.exception.UserNotPurchasedProductException;
import com.example.usermanagement.exception.DuplicateReviewException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public ReviewDTO addReview(Long userId, Long productId, int rating, String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        // Kiểm tra đã mua hàng và đã giao hàng (DELIVERED) chưa
        List<Order> orders = orderRepository.findByUser(user);
        boolean hasPurchasedDelivered = orders.stream()
                .filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(productId));
        if (!hasPurchasedDelivered) {
            throw new UserNotPurchasedProductException("User must have at least one DELIVERED order containing this product to review");
        }
        // Kiểm tra đã review chưa
        Optional<Review> existing = reviewRepository.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            throw new DuplicateReviewException("User has already reviewed this product");
        }
        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();
        review = reviewRepository.save(review);
        updateProductAverageRating(productId);
        return ReviewMapper.toDTO(review);
    }

    private void updateProductAverageRating(Long productId) {
        Double averageRating = reviewRepository.findAverageRatingByProductId(productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setAverageRating(averageRating);
        productRepository.save(product);
    }

    @Override
    public List<ReviewDTO> getReviewsByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return reviewRepository.findByProduct(product)
                .stream().map(ReviewMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public Double getAverageRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return reviewRepository.findAverageRatingByProduct(product);
    }
} 