package com.example.service;

import com.example.model.entity.Order;
import com.example.model.dto.OrderRequestDTO;
import com.example.model.enums.OrderStatus;
import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();

    Order getOrderById(Long id);

    List<Order> getOrdersByUserName(String userName);

    List<Order> getOrdersByStatus(OrderStatus status);

    void cancelOrder(Long orderId);

    Order placeOrder(OrderRequestDTO orderRequest);
}
