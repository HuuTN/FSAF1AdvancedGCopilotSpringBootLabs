package com.example.service;

import com.example.entity.Order;
import com.example.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElse(null);
    }

    @Override
    public List<Order> getOrdersByUserName(String userName) {
        return orderRepository.findByUser_Name(userName);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }
    // Implementing the cancelOrder method
    @Override
    public void cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus().equals("PENDING")) {
                order.setStatus("CANCELLED");
                // Assuming there's a method to restore product stock
                // productRepository.restoreStock(order.getProduct().getId());
                orderRepository.save(order);
            } else {
                throw new IllegalStateException("Order cannot be cancelled as it is not in PENDING status");
            }
        } else {
            throw new IllegalStateException("Order not found with id: " + orderId);
        }
    }
    // Additional methods can be implemented as needed
}
