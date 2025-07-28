package com.example.copilot.core.repository;

import com.example.copilot.core.entity.Order;
import com.example.copilot.core.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Check if a user has purchased a specific product with DELIVERED status
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END " +
           "FROM Order o JOIN o.orderItems oi " +
           "WHERE o.user.id = :userId " +
           "AND oi.product.id = :productId " +
           "AND o.status = :status")
    boolean existsByUserIdAndOrderItemsProductIdAndStatus(
        @Param("userId") Long userId, 
        @Param("productId") Long productId, 
        @Param("status") OrderStatus status);

    /**
     * Get dashboard statistics using a single efficient native SQL query
     * Following Optimized Prompt: single database call for multiple statistics
     */
    @Query(value = "SELECT " +
                   "(SELECT COALESCE(SUM(oi.quantity * oi.price), 0) " +
                   " FROM orders o JOIN order_item oi ON o.id = oi.order_id " +
                   " WHERE o.status = 'DELIVERED') as totalRevenue, " +
                   "(SELECT COUNT(*) FROM orders) as totalOrders, " +
                   "(SELECT COUNT(*) FROM user " +
                   " WHERE MONTH(creation_date) = MONTH(CURRENT_DATE()) " +
                   " AND YEAR(creation_date) = YEAR(CURRENT_DATE())) as newCustomersThisMonth", 
           nativeQuery = true)
    Object[] getDashboardStats();
}
