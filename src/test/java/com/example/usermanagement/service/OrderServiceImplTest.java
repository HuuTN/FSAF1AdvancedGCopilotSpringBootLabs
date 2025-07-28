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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {
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
    private Order order;
    private OrderItem item;
    private OrderPostRequest request;
    private OrderItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).name("User").build();
        product = Product.builder().id(2L).name("Prod").price(BigDecimal.TEN).stock(5).build();
        item = OrderItem.builder().id(3L).product(product).quantity(2).price(BigDecimal.TEN).build();
        order = Order.builder().id(4L).user(user).status(OrderStatus.PENDING).orderItems(List.of(item)).build();
        item.setOrder(order);
        request = new OrderPostRequest();
        request.setUserId(1L);
        itemRequest = new OrderItemRequest();
        itemRequest.setProductId(2L);
        itemRequest.setQuantity(2);
        request.setItems(java.util.List.of(itemRequest));
    }

    @Test
    void createOrder_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_ProductNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_NotEnoughStock_ThrowsException() {
        product.setStock(1);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(request));
    }

    @Test
    void cancelOrder_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> orderService.cancelOrder(4L));
    }

    @Test
    void cancelOrder_NotPending_ThrowsException() {
        order.setStatus(OrderStatus.DONE);
        when(orderRepository.findById(4L)).thenReturn(Optional.of(order));
        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(4L));
    }

    @Test
    void updateOrderStatus_OrderNotFound_ThrowsException() {
        when(orderRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrderStatus(4L, OrderStatus.CONFIRMED));
    }
} 