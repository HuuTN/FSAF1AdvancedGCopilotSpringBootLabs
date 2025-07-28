package com.example.demo.services.serviceImpl;

import com.example.demo.core.dtos.ProductReviewDTO;
import com.example.demo.core.entity.Product;
import com.example.demo.core.entity.ProductReview;
import com.example.demo.core.entity.User;
import com.example.demo.core.repository.OrderRepository;
import com.example.demo.core.repository.ProductRepository;
import com.example.demo.core.repository.ProductReviewRepository;
import com.example.demo.core.repository.UserRepository;
import com.example.demo.exception.DuplicateReviewException;
import com.example.demo.exception.UserNotPurchasedProductException;
import com.example.demo.services.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {
    
    @Autowired
    private ProductReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public ProductReviewDTO createReview(ProductReviewDTO reviewDTO) {
        // 1. First, call the orderRepository to verify that the user has at least one completed ('DELIVERED') order containing the product
        if (!reviewRepository.hasUserPurchasedProduct(reviewDTO.getUserId(), reviewDTO.getProductId())) {
            throw new UserNotPurchasedProductException("User must purchase the product before reviewing");
        }
        
        // 2. Then, call the reviewRepository to check if a review from this user for this product already exists
        if (reviewRepository.existsByUserIdAndProductId(reviewDTO.getUserId(), reviewDTO.getProductId())) {
            throw new DuplicateReviewException("User has already reviewed this product");
        }
        
        // 3. If all checks pass, map the DTO to a new Review entity and save it
        User user = userRepository.findById(reviewDTO.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Product product = productRepository.findById(reviewDTO.getProductId())
            .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        ProductReview review = new ProductReview(user, product, reviewDTO.getRating(), reviewDTO.getComment());
        review = reviewRepository.save(review);
        
        // Update product average rating
        updateProductAverageRating(reviewDTO.getProductId());
        
        return toDTO(review);
    }

    @Override
    @Transactional
    public ProductReviewDTO updateReview(Long reviewId, ProductReviewDTO reviewDTO, Long userId) {
        ProductReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User can only update their own reviews");
        }
        
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review = reviewRepository.save(review);
        
        // Update product average rating
        updateProductAverageRating(review.getProduct().getId());
        
        return toDTO(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        ProductReview review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User can only delete their own reviews");
        }
        
        Long productId = review.getProduct().getId();
        reviewRepository.delete(review);
        
        // Update product average rating
        updateProductAverageRating(productId);
    }

    @Override
    public Page<ProductReviewDTO> getReviewsByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdOrderByReviewDateDesc(productId, pageable)
            .map(this::toDTO);
    }

    @Override
    public Page<ProductReviewDTO> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserIdOrderByReviewDateDesc(userId, pageable)
            .map(this::toDTO);
    }

    @Override
    public ProductReviewDTO getUserReviewForProduct(Long userId, Long productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId)
            .map(this::toDTO)
            .orElse(null);
    }

    @Override
    public boolean canUserReviewProduct(Long userId, Long productId) {
        return reviewRepository.hasUserPurchasedProduct(userId, productId) &&
               !reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    /**
     * Private helper method to update product average rating using JPQL for efficiency
     * @param productId The ID of the product to update
     */
    @Transactional
    private void updateProductAverageRating(Long productId) {
        // Use JPQL query in ReviewRepository to calculate the average rating directly in the database for efficiency
        Double avgRating = reviewRepository.calculateAverageRating(productId);
        long reviewCount = reviewRepository.countByProductId(productId);
        
        // Update the Product using JPQL update query
        reviewRepository.updateProductAverageRating(
            productId,
            avgRating != null ? avgRating : 0.0,
            (int) reviewCount
        );
    }

    private ProductReviewDTO toDTO(ProductReview review) {
        return new ProductReviewDTO(
            review.getId(),
            review.getUser().getId(),
            review.getProduct().getId(),
            review.getRating(),
            review.getComment(),
            review.getReviewDate(),
            review.getUser().getName(),
            review.getProduct().getName()
        );
    }
}
