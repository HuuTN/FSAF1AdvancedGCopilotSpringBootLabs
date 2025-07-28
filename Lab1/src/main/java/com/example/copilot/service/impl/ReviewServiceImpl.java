package com.example.copilot.service.impl;

import com.example.copilot.core.constants.ApplicationConstants;
import com.example.copilot.core.dto.ReviewDTO;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.entity.Review;
import com.example.copilot.core.entity.User;
import com.example.copilot.core.enums.OrderStatus;
import com.example.copilot.core.repository.OrderRepository;
import com.example.copilot.core.repository.ProductRepository;
import com.example.copilot.core.repository.UserRepository;
import com.example.copilot.exception.DuplicateReviewException;
import com.example.copilot.exception.ResourceNotFoundException;
import com.example.copilot.exception.UserNotPurchasedProductException;
import com.example.copilot.repository.ReviewRepository;
import com.example.copilot.service.OrderVerificationService;
import com.example.copilot.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderVerificationService orderVerificationService;
    
    /**
     * Implementation following TDD approach with optimized logic
     * 1. First, call the orderRepository to verify that the user (userId) has at least one 
     *    completed ('DELIVERED') order containing the product (productId). 
     *    If not, throw a new 'UserNotPurchasedProductException'.
     * 2. Then, call the reviewRepository to check if a review from this user for this product 
     *    already exists. If yes, throw a 'DuplicateReviewException'.
     * 3. If all checks pass, map the DTO to a new Review entity and save it.
     */
    @Override
    public ReviewDTO addReview(ReviewDTO reviewDTO) {
        Long userId = reviewDTO.getUserId();
        Long productId = reviewDTO.getProductId();
        
        // 1. Verify user has purchased the product with DELIVERED status
        boolean hasPurchased = orderVerificationService.hasUserPurchasedProduct(userId, productId);
        if (!hasPurchased) {
            throw new UserNotPurchasedProductException(
                ApplicationConstants.ErrorMessages.USER_NOT_PURCHASED);
        }
        
        // 2. Check if user has already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateReviewException(
                ApplicationConstants.ErrorMessages.DUPLICATE_REVIEW);
        }
        
        // 3. Fetch user and product entities
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ApplicationConstants.ErrorMessages.USER_NOT_FOUND + userId));
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                ApplicationConstants.ErrorMessages.PRODUCT_NOT_FOUND + productId));
        
        // 4. Map DTO to entity and save
        Review review = mapDtoToEntity(reviewDTO);
        review.setUser(user);
        review.setProduct(product);
        
        Review savedReview = reviewRepository.save(review);
        
        // 5. Optimized Prompt: Update product average rating using efficient helper method
        this.updateProductAverageRating(productId);
        
        return mapEntityToDto(savedReview);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getProductReviews(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getUserReviews(Long userId) {
        List<Review> reviews = reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return reviews.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateProductRating(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        Double averageRating = reviewRepository.calculateAverageRating(productId);
        Integer reviewCount = reviewRepository.countReviewsByProductId(productId);
        
        // Update product with new ratings
        product.setAverageRating(averageRating != null ? averageRating : 0.0);
        product.setReviewCount(reviewCount != null ? reviewCount : 0);
        
        productRepository.save(product);
    }
    
    /**
     * Optimized Prompt: Private helper method for updating product average rating
     * This helper method accepts a productId and uses efficient JPQL query
     * to calculate average rating directly in the database for better performance
     */
    private void updateProductAverageRating(Long productId) {
        // Use efficient JPQL query with Optional wrapper for null safety
        Double averageRating = reviewRepository.getAverageRatingByProductId(productId).orElse(0.0);
        
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        product.setAverageRating(averageRating);
        productRepository.save(product);
    }
    
    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        // Check if the review belongs to the user
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User can only delete their own reviews");
        }
        
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        
        // Update product rating after deletion using optimized helper method
        this.updateProductAverageRating(productId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(
            userId, productId, OrderStatus.DELIVERED);
    }
    
    /**
     * Helper method to map DTO to Entity
     */
    private Review mapDtoToEntity(ReviewDTO dto) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return review;
    }
    
    /**
     * Helper method to map Entity to DTO
     */
    private ReviewDTO mapEntityToDto(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserFullName(review.getUser().getName()); // User entity has 'name' field
        dto.setProductId(review.getProduct().getId());
        dto.setProductName(review.getProduct().getName());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }
}
