package com.example.repository;

import com.example.entity.Order;
import com.example.repository.projection.DashboardStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser_Name(String userName);
    List<Order> findByStatus(String status);
    
    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.orderItems oi " +
           "WHERE o.user.id = :userId AND oi.product.id = :productId " +
           "AND o.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Query(nativeQuery = true, value = """
        SELECT 
            COALESCE(SUM(oi.price * oi.quantity), 0) as totalRevenue,
            COUNT(DISTINCT o.id) as totalOrders,
            (
                SELECT COUNT(DISTINCT u.id)
                FROM users u
                WHERE DATE_FORMAT(u.created_date, '%Y-%m') = DATE_FORMAT(CURRENT_DATE, '%Y-%m')
            ) as newCustomersThisMonth
        FROM orders o
        LEFT JOIN order_items oi ON o.id = oi.order_id
        WHERE o.status = 'DELIVERED'
    """)
    DashboardStats getDashboardStats();
}
