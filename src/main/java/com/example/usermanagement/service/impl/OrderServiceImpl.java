package com.example.usermanagement.service.impl;

import com.example.usermanagement.constant.OrderStatus;
import com.example.usermanagement.entity.Order;
import com.example.usermanagement.entity.OrderItem;
import com.example.usermanagement.entity.Product;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.EntityNotFoundException;
import com.example.usermanagement.exception.InsufficientStockException;
import com.example.usermanagement.repository.OrderItemRepository;
import com.example.usermanagement.repository.OrderRepository;
import com.example.usermanagement.repository.ProductRepository;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import com.example.usermanagement.dto.OrderPostRequest;
import com.example.usermanagement.dto.OrderItemRequest;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    // This method was already refactor before and it is ok now, no need to update.
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderPostRequest request) {
        User user = validateAndGetUser(request.getUserId());
        List<OrderItem> orderItems = request.getItems().stream()
                .map(this::processOrderItem)
                .toList();
        Order order = buildOrder(user, orderItems);
        saveOrderAndItems(order, orderItems);
        return order;
    }

    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private OrderItem processOrderItem(OrderItemRequest itemReq) {
        Product product = productRepository.findById(itemReq.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + itemReq.getProductId()));
        if (product.getStock() < itemReq.getQuantity()) {
            throw new InsufficientStockException("Not enough product in stock for productId: " + itemReq.getProductId());
        }
        product.setStock(product.getStock() - itemReq.getQuantity());
        productRepository.save(product);
        return OrderItem.builder()
                .product(product)
                .quantity(itemReq.getQuantity())
                .price(product.getPrice())
                .build();
    }

    private Order buildOrder(User user, List<OrderItem> orderItems) {
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .orderItems(orderItems)
                .build();
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        return order;
    }

    private void saveOrderAndItems(Order order, List<OrderItem> orderItems) {
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
        orderRepository.save(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
} 