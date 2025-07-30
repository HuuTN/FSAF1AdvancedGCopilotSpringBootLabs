package com.example.demo.repository;

import com.example.demo.domain.ReviewStats;
import com.example.demo.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // @Query("SELECT COUNT(r), COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.product.id = :productId")
    // Object[] getReviewStatsForProduct(@Param("productId") Long productId);
    @Query("SELECT COUNT(r) AS count, COALESCE(AVG(r.rating), 0.0) AS avg FROM Review r WHERE r.product.id = :productId")
    ReviewStats getReviewStatsForProduct(@Param("productId") Long productId);
    List<Review> findByProductId(Long productId);
    List<Review> findByUserId(Long userId);
    boolean existsByOrderItemId(Long orderItemId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
