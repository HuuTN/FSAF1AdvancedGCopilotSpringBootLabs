package com.example.demo.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.OrderStatus;
import com.example.demo.dto.CreateOrderRequestDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.OrderItemDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OrderDTO placeOrder(CreateOrderRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderRequestDTO.OrderItemRequestDTO itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice()); // price snapshot
            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        // Convert to DTO (hoặc dùng MapStruct/ModelMapper cho thực tế)
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(savedOrder.getId());
        orderDTO.setOrderDate(savedOrder.getOrderDate());
        orderDTO.setStatus(savedOrder.getStatus());
        orderDTO.setUserId(savedOrder.getUser().getId());
        List<OrderItemDTO> itemDTOs = orderItems.stream().map(oi -> {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setProduct(oi.getProduct());
            dto.setQuantity(oi.getQuantity());
            dto.setPrice(oi.getPrice());
            return dto;
        }).collect(Collectors.toList());
        orderDTO.setOrderItems(itemDTOs);

        return orderDTO;

    }

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setOrderDate(order.getOrderDate());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setUserId(order.getUser().getId());
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream().map(oi -> {
                OrderItemDTO dto = new OrderItemDTO();
                dto.setProduct(oi.getProduct());
                dto.setQuantity(oi.getQuantity());
                dto.setPrice(oi.getPrice());
                return dto;
            }).collect(Collectors.toList());
            orderDTO.setOrderItems(itemDTOs);
            return orderDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> userOrders = orderRepository.findByUserId(userId);
        return userOrders.stream().map(order -> {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setId(order.getId());
            orderDTO.setOrderDate(order.getOrderDate());
            orderDTO.setStatus(order.getStatus());
            orderDTO.setUserId(order.getUser().getId());
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream().map(oi -> {
                OrderItemDTO dto = new OrderItemDTO();
                dto.setProduct(oi.getProduct());
                dto.setQuantity(oi.getQuantity());
                dto.setPrice(oi.getPrice());
                return dto;
            }).collect(Collectors.toList());
            orderDTO.setOrderItems(itemDTOs);
            return orderDTO;
        }).collect(Collectors.toList());

    }
}
