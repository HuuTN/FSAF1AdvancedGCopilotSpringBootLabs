package com.example.service;

import com.example.dto.CreateOrderRequestDTO;
import com.example.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    // Place a new order
    OrderDTO placeOrder(CreateOrderRequestDTO request);
    // Get all orders
    List<OrderDTO> getAllOrders();
    // Get orders by user ID
    List<OrderDTO> getOrdersByUser(Long userId);
    // Cancel a pending order by ID
    void cancelOrder(Long orderId);
    // ...other methods...
}
