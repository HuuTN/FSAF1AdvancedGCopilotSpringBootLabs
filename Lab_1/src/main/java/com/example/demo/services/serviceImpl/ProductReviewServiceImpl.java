package com.example.demo.services.serviceImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.cores.dtos.CreateProductReviewDTO;
import com.example.demo.cores.dtos.ProductReviewDTO;
import com.example.demo.cores.entity.Product;
import com.example.demo.cores.entity.ProductReview;
import com.example.demo.cores.repository.ProductRepository;
import com.example.demo.cores.repository.ProductReviewRepository;
import com.example.demo.exception.DuplicateReviewException;
import com.example.demo.exception.UserNotPurchasedProductException;
import com.example.demo.services.service.ProductReviewService;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {
    
    @Autowired
    private ProductReviewRepository productReviewRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    @Transactional
    public ProductReviewDTO createReview(Long userId, CreateProductReviewDTO createReviewDTO) {
        // Step 1: Verify that the user has at least one completed ('DELIVERED') order containing the product
        boolean hasPurchased = productReviewRepository.hasUserPurchasedProduct(
            createReviewDTO.getProductId(), 
            userId
        );
        
        if (!hasPurchased) {
            throw new UserNotPurchasedProductException(
                "User must have purchased and received the product to write a review. Product ID: " + 
                createReviewDTO.getProductId() + ", User ID: " + userId
            );
        }
        
        // Step 2: Check if a review from this user for this product already exists
        boolean reviewExists = productReviewRepository.existsByProductIdAndUserId(
            createReviewDTO.getProductId(), 
            userId
        );
        
        if (reviewExists) {
            throw new DuplicateReviewException(
                "User has already reviewed this product. Product ID: " + 
                createReviewDTO.getProductId() + ", User ID: " + userId
            );
        }
        
        // Step 3: Verify the product exists
        Product product = productRepository.findById(createReviewDTO.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + createReviewDTO.getProductId()));
        
        // Step 4: Map the DTO to a new Review entity and save it
        ProductReview review = new ProductReview();
        review.setProduct(product);
        review.setUserId(userId);
        review.setRating(createReviewDTO.getRating());
        review.setComment(createReviewDTO.getComment());
        review.setIsVerifiedPurchase(true); // Since we verified the purchase
        review.setCreatedAt(new Date());
        review.setUpdatedAt(new Date());
        
        ProductReview savedReview = productReviewRepository.save(review);
        
        // Update the product's average rating after saving the review
        updateProductAverageRating(createReviewDTO.getProductId());
        
        return convertToDTO(savedReview);
    }
    
    @Override
    public Page<ProductReviewDTO> getProductReviews(Long productId, Pageable pageable) {
        Page<ProductReview> reviews = productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable);
        return reviews.map(this::convertToDTO);
    }
    
    @Override
    public Page<ProductReviewDTO> getUserReviews(Long userId, Pageable pageable) {
        Page<ProductReview> reviews = productReviewRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return reviews.map(this::convertToDTO);
    }
    
    @Override
    public Page<ProductReviewDTO> getProductReviewsByRating(Long productId, Integer rating, Pageable pageable) {
        Page<ProductReview> reviews = productReviewRepository.findByProductIdAndRatingOrderByCreatedAtDesc(productId, rating, pageable);
        return reviews.map(this::convertToDTO);
    }
    
    @Override
    public Page<ProductReviewDTO> getVerifiedProductReviews(Long productId, Pageable pageable) {
        Page<ProductReview> reviews = productReviewRepository.findByProductIdAndIsVerifiedPurchaseTrueOrderByCreatedAtDesc(productId, pageable);
        return reviews.map(this::convertToDTO);
    }
    
    @Override
    public Double getProductAverageRating(Long productId) {
        Double avgRating = productReviewRepository.findAverageRatingByProductId(productId);
        return avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
    }
    
    @Override
    public Long getProductTotalReviews(Long productId) {
        return productReviewRepository.countByProductId(productId);
    }
    
    private ProductReviewDTO convertToDTO(ProductReview review) {
        ProductReviewDTO dto = new ProductReviewDTO();
        dto.setId(review.getId());
        dto.setProductId(review.getProduct().getId());
        dto.setProductName(review.getProduct().getName());
        dto.setUserId(review.getUserId());
        // You might want to add user name lookup here if needed
        dto.setUserName("User " + review.getUserId()); // Placeholder
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setIsVerifiedPurchase(review.getIsVerifiedPurchase());
        
        // Convert Date to LocalDateTime (from Auditable)
        if (review.getCreatedAt() != null) {
            dto.setCreatedDate(review.getCreatedAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        if (review.getUpdatedAt() != null) {
            dto.setUpdatedDate(review.getUpdatedAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime());
        }
        
        return dto;
    }
    
    /**
     * Private helper method to update the product's average rating
     * after a new review is added.
     */
    private void updateProductAverageRating(Long productId) {
        // Calculate average rating using JPQL query for efficiency
        Double averageRating = productReviewRepository.findAverageRatingByProductId(productId);
        
        // Update the product with the new average rating
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));
        
        // Set the average rating (null-safe, default to 0.0)
        product.setAverageRating(averageRating != null ? 
            Math.round(averageRating * 10.0) / 10.0 : 0.0);
        
        productRepository.save(product);
    }
}
