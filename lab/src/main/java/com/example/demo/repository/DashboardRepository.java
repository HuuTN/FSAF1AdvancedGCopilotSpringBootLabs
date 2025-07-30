package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface DashboardRepository extends Repository<Object, Long> {
    @Query(value = "SELECT " +
            "COALESCE(SUM(CASE WHEN o.status = 'DELIVERED' THEN oi.price * oi.quantity ELSE 0 END),0) AS totalRevenue, " +
            "COUNT(DISTINCT o.id) AS totalOrders, " +
            "(SELECT COUNT(*) FROM user u WHERE YEAR(u.created_at) = YEAR(CURRENT_DATE()) AND MONTH(u.created_at) = MONTH(CURRENT_DATE())) AS newCustomersThisMonth " +
            "FROM orders o " +
            "LEFT JOIN orderitem oi ON o.id = oi.order_id", 
            nativeQuery = true)
    Object[] getDashboardStatsRaw();
}
