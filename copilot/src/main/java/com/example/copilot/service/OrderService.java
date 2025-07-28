package com.example.copilot.service;

import java.util.List;

import com.example.copilot.dto.CreateOrderRequestDTO;
import com.example.copilot.dto.OrderDTO;

public interface OrderService {
    OrderDTO createOrder(CreateOrderRequestDTO request);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getOrdersByUserId(Long userId);
    List<OrderDTO> getAllOrders();


    OrderDTO placeOrder(CreateOrderRequestDTO request);
}

