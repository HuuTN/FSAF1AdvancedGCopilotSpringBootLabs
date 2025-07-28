package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    @Query("SELECT oi FROM OrderItem oi " +
           "WHERE oi.order.user.id = :userId " +
           "AND oi.product.id = :productId " +
           "AND oi.order.status = 'DELIVERED' " +
           "AND NOT EXISTS (SELECT r FROM Review r WHERE r.orderItem = oi) " +
           "ORDER BY oi.order.orderDate DESC")
    Optional<OrderItem> findFirstByUserIdAndProductIdAndNotReviewed(
        @Param("userId") Long userId, 
        @Param("productId") Long productId);

}
