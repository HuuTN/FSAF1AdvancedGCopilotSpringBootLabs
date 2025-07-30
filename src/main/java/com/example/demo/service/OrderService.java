package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.CreateOrderRequestDTO;
import com.example.demo.dto.OrderDTO;

public interface OrderService {
    OrderDTO placeOrder(CreateOrderRequestDTO request);

    List<OrderDTO> getAllOrders();

    List<OrderDTO> getUserOrders(Long userId);
}
