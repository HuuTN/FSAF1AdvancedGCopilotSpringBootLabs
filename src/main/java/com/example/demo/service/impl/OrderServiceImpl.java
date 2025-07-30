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
        Order order = createOrderFromRequest(request);
        Order savedOrder = orderRepository.save(order);
        return convertToDTO(savedOrder);
    }

    private Order createOrderFromRequest(CreateOrderRequestDTO request) {
        User user = verifyUserExists(request.getUserId());
        Order order = buildOrder(user);
        List<OrderItem> orderItems = buildOrderItems(request.getItems(), order);
        order.setOrderItems(orderItems);
        return order;
    }

    private User verifyUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Order buildOrder(User user) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);
        return order;
    }

    private List<OrderItem> buildOrderItems(List<CreateOrderRequestDTO.OrderItemRequestDTO> itemRequests, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderRequestDTO.OrderItemRequestDTO itemRequest : itemRequests) {
            Product product = verifyAndUpdateStock(itemRequest);
            orderItems.add(createOrderItem(order, product, itemRequest));
        }
        return orderItems;
    }

    private Product verifyAndUpdateStock(CreateOrderRequestDTO.OrderItemRequestDTO itemRequest) {
        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStockQuantity() < itemRequest.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }

        product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
        return productRepository.save(product);
    }

    private OrderItem createOrderItem(Order order, Product product, CreateOrderRequestDTO.OrderItemRequestDTO itemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setPrice(product.getPrice());
        return orderItem;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setUserId(order.getUser().getId());
        
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
            .map(this::convertOrderItemToDTO)
            .collect(Collectors.toList());
        orderDTO.setOrderItems(itemDTOs);
        return orderDTO;
    }

    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setProduct(orderItem.getProduct());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPrice(orderItem.getPrice());
        return dto;
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> userOrders = orderRepository.findByUserId(userId);
        return userOrders.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
}
