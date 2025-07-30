package com.example.demo.core.repository;

import com.example.demo.core.dtos.DashboardStatsDTO;
import com.example.demo.core.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT o.* FROM orders o JOIN users u ON o.user_id = u.id WHERE u.id = :userId", nativeQuery = true)
    List<Order> findOrdersByUserIdNative(@Param("userId") Long userId);
    
    // Dashboard statistics query - fetches multiple stats in single native SQL query
    @Query(value = """
        SELECT
            COALESCE(SUM(oi.quantity * p.price), 0) as totalRevenue,
            COALESCE((SELECT COUNT(*) FROM orders), 0) as totalOrders,
            COALESCE((SELECT COUNT(*) FROM users 
             WHERE YEAR(created_at) = YEAR(CURRENT_DATE) 
             AND MONTH(created_at) = MONTH(CURRENT_DATE)), 0) as newCustomersThisMonth
        FROM orders o
        JOIN order_items oi ON o.id = oi.order_id
        JOIN products p ON oi.product_id = p.id
        WHERE o.status = 'DELIVERED'
        """, nativeQuery = true)
    DashboardStatsDTO getDashboardStats();
}
