package com.example.copilot.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.copilot.dto.CreateOrderRequestDTO;
import com.example.copilot.dto.OrderDTO;
import com.example.copilot.entity.Order;
import com.example.copilot.entity.OrderItem;
import com.example.copilot.entity.Product;
import com.example.copilot.exception.InsufficientStockException;
import com.example.copilot.repository.OrderRepository;
import com.example.copilot.repository.ProductRepository;
import com.example.copilot.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public OrderDTO createOrder(CreateOrderRequestDTO request) {
        return placeOrder(request);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return mapOrderToDTO(order);
    }

    @Override
    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findAll().stream()
            .filter(o -> o.getUser() != null && o.getUser().getId().equals(userId))
            .collect(Collectors.toList());
        return orders.stream().map(this::mapOrderToDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(this::mapOrderToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO placeOrder(CreateOrderRequestDTO request) {
        validateStockAvailability(request);
        Order order = createAndSaveOrder(request);
        updateProductStock(request);
        return mapOrderToDTO(order);
    }

    // Checks if all products in the order have sufficient stock
    private void validateStockAvailability(CreateOrderRequestDTO request) {
        request.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getId());
            }
        });
    }

    // Creates and saves the Order entity
    private Order createAndSaveOrder(CreateOrderRequestDTO request) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        // Set user if needed: order.setUser(...);

        List<OrderItem> orderItems = request.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(java.math.BigDecimal.valueOf(product.getPrice()));
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }

    // Updates product stock after order placement
    private void updateProductStock(CreateOrderRequestDTO request) {
        request.getItems().forEach(item -> {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        });
    }

    // Maps Order entity to OrderDTO
    private OrderDTO mapOrderToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus().name());     
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        return dto;
    }
}