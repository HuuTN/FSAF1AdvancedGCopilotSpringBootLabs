package com.example.demo.core.repository;

import com.example.demo.core.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    
    // Check if user has purchased the product (through completed orders)
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.user.id = :userId " +
           "AND oi.product.id = :productId " +
           "AND o.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
    
    // Check if user already reviewed the product
    boolean existsByUserIdAndProductId(Long userId, Long productId);
    
    // Get reviews for a product
    Page<ProductReview> findByProductIdOrderByReviewDateDesc(Long productId, Pageable pageable);
    
    // Get reviews by a user
    Page<ProductReview> findByUserIdOrderByReviewDateDesc(Long userId, Pageable pageable);
    
    // Calculate average rating for a product
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product.id = :productId")
    Double calculateAverageRating(@Param("productId") Long productId);
    
    // Count reviews for a product
    long countByProductId(Long productId);
    
    // Update product average rating using JPQL
    @Modifying
    @Query("UPDATE Product p SET p.averageRating = :avgRating, p.reviewCount = :reviewCount WHERE p.id = :productId")
    void updateProductAverageRating(@Param("productId") Long productId, @Param("avgRating") Double avgRating, @Param("reviewCount") Integer reviewCount);
    
    // Get user's review for a specific product
    Optional<ProductReview> findByUserIdAndProductId(Long userId, Long productId);
}
