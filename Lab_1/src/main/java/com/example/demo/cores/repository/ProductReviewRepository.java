package com.example.demo.cores.repository;

import com.example.demo.cores.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    
    // Find reviews by product with pagination
    Page<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    // Find reviews by user
    Page<ProductReview> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Check if user already reviewed the product in this order
    boolean existsByProductIdAndUserIdAndOrderId(Long productId, Long userId, Long orderId);
    
    // Get average rating for a product
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    
    // Count total reviews for a product
    long countByProductId(Long productId);
    
    // Find reviews by rating
    Page<ProductReview> findByProductIdAndRatingOrderByCreatedAtDesc(Long productId, Integer rating, Pageable pageable);
    
    // Find verified purchase reviews only
    Page<ProductReview> findByProductIdAndIsVerifiedPurchaseTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);
    
    // Get rating distribution for a product
    @Query("SELECT pr.rating, COUNT(pr) FROM ProductReview pr WHERE pr.product.id = :productId GROUP BY pr.rating ORDER BY pr.rating")
    List<Object[]> findRatingDistributionByProductId(@Param("productId") Long productId);
    
    // Check if user has purchased the product in any delivered order
    @Query("SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END " +
           "FROM OrderItem oi WHERE oi.product.id = :productId " +
           "AND oi.order.user.id = :userId AND oi.order.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("productId") Long productId, @Param("userId") Long userId);
    
    // Check if user already reviewed this product
    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
