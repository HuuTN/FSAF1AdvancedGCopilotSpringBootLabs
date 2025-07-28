package com.example.repository;

import com.example.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    
    @Query(value = """
            SELECT COALESCE(AVG(r.rating), 0.0) as avgRating, 
                   COUNT(r.id) as totalReviews 
            FROM Review r 
            WHERE r.product.id = :productId
            """)
    ReviewStats calculateReviewStats(@Param("productId") Long productId);
    
    interface ReviewStats {
        Double getAvgRating();
        Long getTotalReviews();
    }
    
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
