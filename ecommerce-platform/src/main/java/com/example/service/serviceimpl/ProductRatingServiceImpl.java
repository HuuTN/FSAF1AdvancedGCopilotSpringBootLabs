package com.example.service.serviceimpl;

import com.example.model.entity.Product;
import com.example.repository.ProductRepository;
import com.example.repository.ReviewRepository;
import com.example.service.ProductRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of ProductRatingService.
 * Handles calculation and updating of product average ratings.
 */
@Service
@Transactional
public class ProductRatingServiceImpl implements ProductRatingService {

    private static final Logger logger = LoggerFactory.getLogger(ProductRatingServiceImpl.class);

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public ProductRatingServiceImpl(ProductRepository productRepository, ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * Updates the average rating for a product based on all its reviews.
     * 
     * @param productId the product ID to update
     */
    @Override
    @Transactional
    public void updateProductAverageRating(Long productId) {
        logger.debug("Updating average rating for product {}", productId);

        try {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));

            Double averageRating = reviewRepository.findAverageRatingByProductId(productId);

            if (averageRating != null) {
                // Round to 2 decimal places
                BigDecimal roundedRating = BigDecimal.valueOf(averageRating)
                        .setScale(2, RoundingMode.HALF_UP);
                product.setAverageRating(roundedRating);
                logger.debug("Updated product {} average rating to {}", productId, roundedRating);
            } else {
                // No reviews yet, set to zero
                product.setAverageRating(BigDecimal.ZERO);
                logger.debug("No reviews found for product {}, set average rating to zero", productId);
            }

            // Update review count as well (if Product entity has reviewCount field)
            // Long reviewCount = reviewRepository.countReviewsByProductId(productId);
            // product.setReviewCount(reviewCount.intValue());

            productRepository.save(product);
            logger.info("Successfully updated average rating for product {}", productId);

        } catch (Exception e) {
            logger.error("Error updating average rating for product {}: {}", productId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Gets the current average rating for a product.
     * 
     * @param productId the product ID
     * @return the average rating, or null if no reviews exist
     */
    @Override
    @Transactional(readOnly = true)
    public Double getProductAverageRating(Long productId) {
        return reviewRepository.findAverageRatingByProductId(productId);
    }

    /**
     * Gets the review count for a product.
     * 
     * @param productId the product ID
     * @return the number of reviews
     */
    @Override
    @Transactional(readOnly = true)
    public Long getProductReviewCount(Long productId) {
        return reviewRepository.countReviewsByProductId(productId);
    }

    /**
     * Batch update ratings for multiple products.
     * Useful for maintenance operations.
     * 
     * @param productIds list of product IDs to update
     */
    @Override
    @Transactional
    public void batchUpdateProductRatings(java.util.List<Long> productIds) {
        logger.info("Starting batch update for {} products", productIds.size());

        for (Long productId : productIds) {
            try {
                updateProductAverageRating(productId);
            } catch (Exception e) {
                logger.error("Failed to update rating for product {}: {}", productId, e.getMessage());
                // Continue with other products
            }
        }

        logger.info("Completed batch update for product ratings");
    }
}
