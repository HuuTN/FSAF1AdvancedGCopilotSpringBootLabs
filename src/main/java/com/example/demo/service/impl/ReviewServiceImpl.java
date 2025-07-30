package com.example.demo.service.impl;

import com.example.demo.domain.ReviewStats;
import com.example.demo.dto.ReviewDTO;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.Review;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserNotPurchasedProductException;
import com.example.demo.exception.DuplicateReviewException;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReviewRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.ReviewService;
import com.example.demo.service.OrderVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderVerificationService orderVerificationService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           OrderVerificationService orderVerificationService,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderVerificationService = orderVerificationService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public ReviewDTO addReview(ReviewDTO dto, Long userId, Long productId) {
        // 1. Verify that the product and user exist
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
        // 2. Check if the user has purchased the product
        if (!orderVerificationService.hasUserPurchasedProduct(userId, productId)) {
            throw new UserNotPurchasedProductException(
                "You can only review products you have purchased and received");
        }

        // 3. Check if user has already reviewed this product
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateReviewException(
                "You have already reviewed this product");
        }

        // 4. Validate rating
        if (dto.getRating() < 1 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // 5. Verify that the order item exists and belongs to the user
        OrderItem orderItem = orderItemRepository.findById(dto.getOrderItemId())
            .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));
        
        // Verify that the order item belongs to the correct user and product
        if (!orderItem.getOrder().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("This order item does not belong to you");
        }
        if (!orderItem.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("This order item is not for the specified product");
        }

        // Create and save the review
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setProduct(product);
        review.setUser(user);
        review.setCreatedAt(LocalDateTime.now());
        review.setOrderItem(orderItem);
        
        Review savedReview = reviewRepository.save(review);
        
        // 6. Update product's average rating
        updateProductAverageRating(product.getId());
        
        return convertToDTO(savedReview);
    }

    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        return reviews.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * Verifies if a user can review a product by checking their purchase history.
     * @param userId The ID of the user attempting to review
     * @param orderItemId The ID of the order item being reviewed
     * @return true if the user has purchased the product and hasn't reviewed it yet
     */
    public boolean canUserReview(Long userId, Long orderItemId) {
        // Check if the review already exists
        if (reviewRepository.existsByOrderItemId(orderItemId)) {
            return false; // User has already reviewed this order item
        }

        // Get the order item and verify it belongs to the user and is in DELIVERED status
        return orderRepository.findByOrderItemIdAndUserId(orderItemId, userId)
            .map(order -> "DELIVERED".equals(order.getStatus()))
            .orElse(false);
    }

    private ReviewDTO convertToDTO(Review review) {
        return new ReviewDTO(
            review.getId(),
            review.getRating(),
            review.getComment(),
            review.getProduct().getId(),
            review.getUser().getId(),
            review.getUser().getName(),
            review.getOrderItem().getId(),
            review.getCreatedAt()
        );
    }

    /**
     * Updates the average rating and total reviews count for a product
     * @param productId The ID of the product to update the rating for
     */
    @Transactional
    protected void updateProductAverageRating(Long productId) {
        // Get review stats directly from the database
        ReviewStats stats = reviewRepository.getReviewStatsForProduct(productId);
        long totalReviews = stats.getCount();
        double averageRating = stats.getAvg();

        // Update the product
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setAverageRating(averageRating);
        product.setTotalReviews((int) totalReviews);
        productRepository.save(product);
    }
}
