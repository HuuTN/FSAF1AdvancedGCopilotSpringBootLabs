package com.example.copilot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.copilot.dto.DashboardStatsDTO;
import com.example.copilot.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // ...existing code...

    @Query("""
        SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
        FROM Order o
        JOIN o.orderItems oi
        WHERE o.user.id = :userId AND oi.product.id = :productId AND o.status = com.example.copilot.entity.Order.Status.DELIVERED
    """)
    boolean existsByUserIdAndProductIdAndStatus(
        @Param("userId") Long userId,
        @Param("productId") Long productId,
        @Param("status") String status // This parameter is not used in the query, but kept for method signature compatibility
    );

    @Query(
        value = """
            SELECT
                IFNULL(SUM(oi.price * oi.quantity), 0) AS totalRevenue,
                (SELECT COUNT(*) FROM orders) AS totalOrders,
                (SELECT COUNT(*) FROM users WHERE YEAR(created_at) = YEAR(CURRENT_DATE()) AND MONTH(created_at) = MONTH(CURRENT_DATE())) AS newCustomersThisMonth
            FROM orders o
            JOIN order_items oi ON o.id = oi.order_id
            WHERE o.status = 'DELIVERED'
            """,
        nativeQuery = true
    )
    DashboardStatsDTO getDashboardStats();

    // ...existing code...
}