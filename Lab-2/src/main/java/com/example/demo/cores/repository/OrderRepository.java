package com.example.demo.cores.repository;

import com.example.demo.cores.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query(value = "SELECT o.* FROM orders o JOIN users u ON o.user_id = u.id WHERE u.id = :userId", nativeQuery = true)
    List<Order> findOrdersByUserIdNative(@Param("userId") Long userId);
    
    /**
     * Efficient native SQL query to fetch multiple dashboard statistics in one database call.
     * Returns: totalRevenue, totalOrders, newCustomersThisMonth
     */
    @Query(value = """
        SELECT 
            COALESCE(SUM(CASE WHEN o.status = 'DELIVERED' THEN oi.price * oi.quantity ELSE 0 END), 0) as totalRevenue,
            COUNT(o.id) as totalOrders,
            (
                SELECT COUNT(DISTINCT u.id) 
                FROM users u 
                WHERE YEAR(u.created_at) = YEAR(CURDATE()) 
                AND MONTH(u.created_at) = MONTH(CURDATE())
            ) as newCustomersThisMonth
        FROM orders o
        LEFT JOIN order_items oi ON o.id = oi.order_id
        """, nativeQuery = true)
    Object[] getDashboardStats();
}
