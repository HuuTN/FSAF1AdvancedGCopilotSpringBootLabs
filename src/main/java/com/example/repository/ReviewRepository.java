package com.example.repository;

import com.example.entity.Review;
import com.example.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends BaseRepository<Review, Long> {
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId")
    Page<Review> findByProductId(@Param("productId") Long productId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId")
    Page<Review> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingForProduct(@Param("productId") Long productId);
    
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating")
    List<Object[]> getRatingDistributionForProduct(@Param("productId") Long productId);
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.rating = :rating")
    List<Review> findByProductIdAndRating(@Param("productId") Long productId, @Param("rating") Integer rating);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countByProductId(@Param("productId") Long productId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Review r WHERE r.product.category.id = :categoryId")
    Page<Review> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    // MySQL does not support parameterized LIMIT in native queries directly. Use Pageable for limiting results.
    @Query(value = "SELECT * FROM review WHERE product_id = :productId ORDER BY rating DESC", nativeQuery = true)
    List<Review> findTopRatedReviews(@Param("productId") Long productId, Pageable pageable);
    
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.user.id = :userId")
    Optional<Review> findByProductIdAndUserId(@Param("productId") Long productId, @Param("userId") Long userId);
    
    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
