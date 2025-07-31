package com.example.usermanagement.service;

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
import com.example.usermanagement.service.impl.OrderServiceImpl;
import com.example.usermanagement.dto.OrderPostRequest;
import com.example.usermanagement.dto.OrderItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTddTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private OrderPostRequest request;
    private OrderItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).name("Test User").email("test@example.com").build();
        product = Product.builder().id(1L).name("Test Product").price(BigDecimal.valueOf(100)).stock(10).build();
        request = new OrderPostRequest();
        request.setUserId(1L);
        itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);
        request.setItems(java.util.List.of(itemRequest));
    }

    @Test
    void createOrder_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = orderService.createOrder(request);
        assertNotNull(order);
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(8, product.getStock());
    }

    @Test
    void createOrder_ProductOutOfStock_ThrowsException() {
        product.setStock(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(request));
    }

    @Test
    void cancelOrder_Pending_Success() {
        Order order = Order.builder().id(1L).user(user).status(OrderStatus.PENDING).build();
        OrderItem item = OrderItem.builder().id(1L).order(order).product(product).quantity(2).build();
        order.setOrderItems(List.of(item));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        orderService.cancelOrder(1L);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(12, product.getStock());
    }

    @Test
    void cancelOrder_NotPending_ThrowsException() {
        Order order = Order.builder().id(1L).user(user).status(OrderStatus.DONE).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));
    }

    @Test
    void updateOrderStatus_Success() {
        Order order = Order.builder().id(1L).user(user).status(OrderStatus.PENDING).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));
        orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }
} 