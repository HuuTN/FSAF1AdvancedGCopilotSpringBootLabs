package com.example.copilot.service;

import com.example.copilot.core.dto.OrderDTO;
import com.example.copilot.core.dto.CreateOrderRequestDTO;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDTO placeOrder(CreateOrderRequestDTO request);
    Optional<OrderDTO> getById(Long id);
    List<OrderDTO> getAll();
    List<OrderDTO> getByUserId(Long userId);
    boolean delete(Long id);
}
