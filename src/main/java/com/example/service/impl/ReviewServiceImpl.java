package com.example.service.impl;

import com.example.dto.ReviewDTO;
import com.example.entity.Order;
import com.example.entity.OrderStatus;
import com.example.entity.Product;
import com.example.entity.Review;
import com.example.entity.User;
import com.example.exception.DuplicateReviewException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.ReviewNotFoundException;
import com.example.exception.UserNotPurchasedProductException;
import com.example.exception.UserNotFoundException;
import com.example.repository.OrderRepository;
import com.example.repository.ProductRepository;
import com.example.repository.ReviewRepository;
import com.example.repository.UserRepository;
import com.example.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        // Input validation
        if (reviewDTO == null) {
            throw new IllegalArgumentException("Review data cannot be null");
        }
        
        if (!reviewDTO.isValidIds()) {
            throw new IllegalArgumentException("Invalid user ID or product ID");
        }
        
        if (!reviewDTO.isValidRating()) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        if (!reviewDTO.isValidComment()) {
            throw new IllegalArgumentException("Comment cannot exceed 1000 characters");
        }
        
        // 1. First, call the orderRepository to verify that the user (userId) has at least one completed ('DELIVERED') order containing the product (productId)
        List<Order> deliveredOrders = orderRepository.findByUserIdAndStatus(reviewDTO.getUserId(), OrderStatus.DELIVERED);
        
        boolean hasDeliveredProduct = deliveredOrders.stream()
            .flatMap(order -> order.getItems().stream())
            .anyMatch(item -> item.getProduct().getId().equals(reviewDTO.getProductId()));
        
        if (!hasDeliveredProduct) {
            throw new UserNotPurchasedProductException("User must have purchased and received the product to review it");
        }

        // 2. Then, call the reviewRepository to check if a review from this user for this product already exists
        if (reviewRepository.existsByProductIdAndUserId(reviewDTO.getProductId(), reviewDTO.getUserId())) {
            throw new DuplicateReviewException("User has already reviewed this product");
        }

        // 3. If all checks pass, map the DTO to a new Review entity and save it
        User user = userRepository.findById(reviewDTO.getUserId())
            .orElseThrow(() -> new UserNotFoundException(reviewDTO.getUserId()));
        Product product = productRepository.findById(reviewDTO.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(reviewDTO.getProductId()));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        Review savedReview = reviewRepository.save(review);
        
        // Now, call a new private helper method named 'updateProductAverageRating'
        updateProductAverageRating(reviewDTO.getProductId());
        
        return mapToDTO(savedReview);
    }

    @Override
    public Page<ReviewDTO> getReviewsByProduct(Long productId, Pageable pageable) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }
        
        return reviewRepository.findByProductId(productId, pageable)
            .map(this::mapToDTO);
    }

    @Override
    public Page<ReviewDTO> getReviewsByUser(Long userId, Pageable pageable) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        return reviewRepository.findByUserId(userId, pageable)
            .map(this::mapToDTO);
    }

    @Override
    public Double getAverageRatingForProduct(Long productId) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }
        
        Double rating = reviewRepository.getAverageRatingForProduct(productId);
        return rating != null ? rating : 0.0;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        if (reviewId == null || reviewId <= 0) {
            throw new IllegalArgumentException("Invalid review ID");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User can only delete their own reviews");
        }

        reviewRepository.delete(review);
        
        // Update product average rating after deletion
        updateProductAverageRating(review.getProduct().getId());
    }

    private ReviewDTO mapToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserName(review.getUser().getUsername()); // Assuming User entity has getUsername() method
        dto.setProductId(review.getProduct().getId());
        dto.setProductName(review.getProduct().getName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
    
    /**
     * This helper method accepts a productId.
     * Inside the helper, use a JPQL query in ReviewRepository to calculate the average rating 
     * directly in the database for efficiency, then update the Product.
     */
    private void updateProductAverageRating(Long productId) {
        productRepository.updateAverageRating(productId);
    }
}
