package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.model.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find all order items by order ID
    List<OrderItem> findByOrder_Id(Long orderId);

    // Find all order items by product ID
    List<OrderItem> findByProduct_Id(Long productId);

    // Find all order items by order ID and product ID
    List<OrderItem> findByOrder_IdAndProduct_Id(Long orderId, Long productId);

    // Find order items by customer (through order relationship)
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.user.name = :userName")
    List<OrderItem> findByCustomerName(@Param("userName") String userName);

    // Find order items by product name
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.name = :productName")
    List<OrderItem> findByProductName(@Param("productName") String productName);

    // Find order items by category (through product relationship)
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.category.name = :categoryName")
    List<OrderItem> findByProductCategory(@Param("categoryName") String categoryName);

    // Calculate total quantity for a specific product
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product.id = :productId")
    Integer getTotalQuantityByProduct(@Param("productId") Long productId);

    // Delete order items by order ID
    void deleteByOrder_Id(Long orderId);
}
