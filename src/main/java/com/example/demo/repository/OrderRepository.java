package com.example.demo.repository;

import com.example.demo.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.dto.TopProductDTO;
import com.example.demo.dto.RecentOrderDTO;
import com.example.demo.dto.DailyRevenueDTO;
import com.example.demo.dto.OrderStatusCount;
import com.example.demo.dto.DashboardStatsDTO;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
    
    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.id = :orderItemId AND o.user.id = :userId")
    Optional<Order> findByOrderItemIdAndUserId(@Param("orderItemId") Long orderItemId, @Param("userId") Long userId);
    
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o " +
           "JOIN o.orderItems oi " +
           "WHERE o.user.id = :userId " +
           "AND oi.product.id = :productId " +
           "AND o.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    // Total revenue in a date range
    @Query("SELECT COALESCE(SUM(oi.price * oi.quantity), 0) FROM Order o " +
           "JOIN o.orderItems oi " +
           "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
           "AND o.status = 'DELIVERED'")
    Double getTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Count of orders by status
    @Query("SELECT o.status as status, COUNT(o) as count FROM Order o " +
           "GROUP BY o.status")
    List<OrderStatusCount> getOrderCountByStatus();

    // Top selling products
    @Query("SELECT NEW com.example.demo.dto.TopProductDTO(" +
           "oi.product.id, oi.product.name, SUM(oi.quantity), SUM(oi.price * oi.quantity)) " +
           "FROM Order o JOIN o.orderItems oi " +
           "WHERE o.status = 'DELIVERED' " +
           "GROUP BY oi.product.id, oi.product.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<TopProductDTO> getTopSellingProducts(Pageable pageable);

    // Recent orders with details
    @Query("SELECT NEW com.example.demo.dto.RecentOrderDTO(" +
           "o.id, o.orderDate, o.status, o.user.name, " +
           "COUNT(oi), SUM(oi.price * oi.quantity)) " +
           "FROM Order o JOIN o.orderItems oi " +
           "GROUP BY o.id, o.orderDate, o.status, o.user.name " +
           "ORDER BY o.orderDate DESC")
    List<RecentOrderDTO> getRecentOrders(Pageable pageable);

    // Daily revenue in a date range
    @Query("SELECT NEW com.example.demo.dto.DailyRevenueDTO(" +
           "FUNCTION('DATE', o.orderDate) as date, " +
           "COUNT(DISTINCT o.id), " +
           "SUM(oi.price * oi.quantity)) " +
           "FROM Order o JOIN o.orderItems oi " +
           "WHERE o.status = 'DELIVERED' " +
           "AND o.orderDate BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE', o.orderDate) " +
           "ORDER BY date")
    List<DailyRevenueDTO> getDailyRevenue(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);

    /**
     * Fetches key dashboard statistics in a single database query
     * Returns:
     * - Total revenue from delivered orders
     * - Total number of orders
     * - New customers registered this month
     */
@Query("SELECT NEW com.example.demo.dto.DashboardStatsDTO(" +
       "COALESCE(SUM(CASE WHEN o.status = 'DELIVERED' THEN oi.price * oi.quantity ELSE 0 END), 0), " +
       "COUNT(DISTINCT o.id), " +
       "COUNT(DISTINCT CASE WHEN YEAR(u.createdAt) = YEAR(CURRENT_DATE()) AND MONTH(u.createdAt) = MONTH(CURRENT_DATE()) THEN u.id ELSE NULL END)) " +
       "FROM Order o " +
       "LEFT JOIN o.orderItems oi " +
       "LEFT JOIN o.user u")
DashboardStatsDTO getDashboardStats();
}
