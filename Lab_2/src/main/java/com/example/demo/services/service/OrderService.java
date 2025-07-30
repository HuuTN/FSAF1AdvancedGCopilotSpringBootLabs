package com.example.demo.services.service;

import com.example.demo.core.dtos.DashboardStatsDTO;
import com.example.demo.core.entity.Order;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

public interface OrderService {
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    Order createOrder(Order order);
    Optional<Order> updateOrder(Long id, Order orderDetails);
    boolean deleteOrder(Long id);

    List<Order> getOrdersByUserId(Long userId);

    @Transactional
    Order placeOrder(com.example.demo.core.dtos.CreateOrderRequestDTO request);
    
    // Get dashboard statistics
    DashboardStatsDTO getDashboardStats();
}
