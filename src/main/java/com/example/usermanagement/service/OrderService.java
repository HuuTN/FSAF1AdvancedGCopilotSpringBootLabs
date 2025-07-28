package com.example.usermanagement.service;

import com.example.usermanagement.constant.OrderStatus;
import com.example.usermanagement.entity.Order;
import com.example.usermanagement.dto.OrderPostRequest;
import java.util.List;

public interface OrderService {
    Order createOrder(OrderPostRequest request);
    void cancelOrder(Long orderId);
    void updateOrderStatus(Long orderId, OrderStatus status);
    List<Order> getOrdersByUserId(Long userId);
}