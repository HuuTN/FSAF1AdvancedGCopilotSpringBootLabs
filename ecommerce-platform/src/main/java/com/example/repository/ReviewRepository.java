package com.example.repository;

import com.example.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProductId(Long productId);

    List<Review> findByCustomerId(Long customerId);

    List<Review> findByRating(Integer rating);

    List<Review> findByProductIdAndCustomerId(Long productId, Long customerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId")
    Long countReviewsByProductId(@Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    List<Review> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);

    @Query("SELECT r FROM Review r WHERE r.customer.id = :customerId ORDER BY r.createdAt DESC")
    List<Review> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") Long customerId);

    /**
     * Check if a customer has already reviewed a specific product.
     * 
     * @param customerId the customer ID
     * @param productId  the product ID
     * @return true if customer has reviewed the product, false otherwise
     */
    boolean existsByCustomer_IdAndProduct_Id(Long customerId, Long productId);
}
