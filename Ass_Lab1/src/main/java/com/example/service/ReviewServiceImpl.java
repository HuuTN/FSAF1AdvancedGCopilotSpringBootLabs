package com.example.service;

import com.example.dto.ReviewDTO;
import com.example.entity.Product;
import com.example.entity.Review;
import com.example.entity.User;
import com.example.exception.DuplicateReviewException;
import com.example.exception.ProductNotFoundException;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.UserNotPurchasedProductException;
import com.example.repository.ProductRepository;
import com.example.repository.ReviewRepository;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderVerificationService orderVerificationService;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                           UserRepository userRepository,
                           ProductRepository productRepository,
                           OrderVerificationService orderVerificationService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderVerificationService = orderVerificationService;
    }

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO, Long userId, Long productId) {
        // 1. Verify product exists first to fail fast
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));

        // 2. Verify that the user has purchased and received the product
        if (!orderVerificationService.hasUserPurchasedProduct(userId, productId)) {
            throw new UserNotPurchasedProductException(
                "User must have a completed purchase of the product before reviewing it");
        }

        // 3. Check for duplicate review
        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new DuplicateReviewException(
                "User has already reviewed this product");
        }

        // 4. Get user entity
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 5. Create and save the review
        Review review = Review.builder()
            .user(user)
            .product(product)
            .rating(reviewDTO.getRating())
            .comment(reviewDTO.getComment())
            .build();

        review = reviewRepository.save(review);
        
        // 6. Update the product's average rating
        updateProductAverageRating(productId);

        return convertToDTO(review);
    }

    @Override
    public List<ReviewDTO> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateProductAverageRating(Long productId) {
        // Get review statistics in a single query
        ReviewRepository.ReviewStats stats = reviewRepository.calculateReviewStats(productId);
        
        // Get and update the product
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        
        // Update both average rating and total reviews count
        product.setAverageRating(stats.getAvgRating());
        product.setTotalReviews(stats.getTotalReviews());
        
        // Save the updated product
        productRepository.save(product);
    }

    @Override
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderVerificationService.hasUserPurchasedProduct(userId, productId);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserName(review.getUser().getName());
        dto.setProductName(review.getProduct().getName());
        dto.setCreatedAt(review.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return dto;
    }
}
