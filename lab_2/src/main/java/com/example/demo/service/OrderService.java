package com.example.demo.service;

import com.example.demo.dto.CreateOrderRequestDTO;
import com.example.demo.entity.Order;

public interface OrderService {
    Order placeOrder(CreateOrderRequestDTO request);
}
