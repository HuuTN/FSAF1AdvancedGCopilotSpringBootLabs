package com.example.demo.services.service;

import com.example.demo.cores.entity.Order;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.cores.dtos.OrderDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.cores.dtos.CreateOrderRequestDTO;

public interface OrderService {
    List<Order> getAllOrders();
    Page<Order> getAllOrders(Pageable pageable);
    Optional<Order> getOrderById(Long id);
    Order createOrder(OrderDTO orderDTO);
    Optional<Order> updateOrder(Long id, OrderDTO orderDTO);
    boolean deleteOrder(Long id);

    List<Order> getOrdersByUserId(Long userId);

    Order placeOrder(CreateOrderRequestDTO createOrderRequestDTO) throws InsufficientStockException;
}
