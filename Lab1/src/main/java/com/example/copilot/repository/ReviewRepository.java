package com.example.copilot.repository;

import com.example.copilot.core.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Check if a review exists for a specific user and product
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Find a review by user ID and product ID
     */
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * Find all reviews for a specific product, ordered by creation date (newest first)
     */
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);
    
    /**
     * Find all reviews by a specific user
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Calculate average rating for a product
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double calculateAverageRating(@Param("productId") Long productId);
    
    /**
     * Optimized Prompt: Get average rating by product ID with Optional wrapper for efficiency
     * This query calculates average rating directly in database for better performance
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Optional<Double> getAverageRatingByProductId(@Param("productId") Long productId);
    
    /**
     * Count total reviews for a product
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Integer countReviewsByProductId(@Param("productId") Long productId);
    
    /**
     * Find reviews with rating greater than or equal to specified value
     */
    List<Review> findByProductIdAndRatingGreaterThanEqual(Long productId, Integer rating);
}
