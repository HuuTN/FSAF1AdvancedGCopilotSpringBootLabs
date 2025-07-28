package com.example.demo.service;

import com.example.demo.dto.CreateOrderRequestDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import com.example.demo.exception.InsufficientStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Order placeOrder(CreateOrderRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);
        order = orderRepository.save(order);

        Set<OrderItem> orderItems = new HashSet<>();
        for (CreateOrderRequestDTO.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem = orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);
        return orderRepository.save(order);
    }
    // Scheduled task: cancel PENDING orders older than 24h
    @Scheduled(cron = "0 0 * * * *") // runs every hour
    public void cancelStalePendingOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        orderRepository.findAll().stream()
            .filter(o -> o.getStatus() == OrderStatus.PENDING && o.getOrderDate().isBefore(cutoff))
            .forEach(o -> {
                o.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(o);
            });
    }
}
