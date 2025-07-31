package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Order;
import com.example.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByUser(User user);

    @Query(value = "SELECT COALESCE(SUM(oi.price * oi.quantity), 0) AS totalRevenue, " +
            "(SELECT COUNT(*) FROM orders) AS totalOrders, " +
            "(SELECT COUNT(*) FROM users WHERE EXTRACT(YEAR FROM created_at) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "AND EXTRACT(MONTH FROM created_at) = EXTRACT(MONTH FROM CURRENT_DATE)) AS newCustomersThisMonth " +
            "FROM orders o " +
            "JOIN order_item oi ON o.id = oi.order_id " +
            "WHERE o.status = 'DELIVERED'", nativeQuery = true)
    Object[] getDashboardStats();
} 