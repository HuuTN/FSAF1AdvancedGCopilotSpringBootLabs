// Write a test method to cancel an PENDING order. Chage the status to CANCELED and restore the stock of the products in the order.
package com.example.service;

import com.example.model.entity.Order;
import com.example.model.entity.OrderItem;
import com.example.model.enums.OrderStatus;
import com.example.repository.OrderRepository;
import com.example.repository.CustomerRepository;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.util.Optional;
import com.example.model.entity.Product;
import com.example.service.serviceimpl.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTddTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private Product product;

    @BeforeEach
    public void setUp() {
        product = new Product();
        product.setId(1L);
        product.setStock(10);

        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.PENDING);
        // Add 2 units of the product to the order using OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        order.setItems(java.util.List.of(orderItem));
    }

    @Test
    public void testCancelPendingOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELED, order.getStatus());

        verify(orderRepository).save(order);
        verify(productService).restoreStock(1L, 2);
    }

    @Test
    public void testCancelShippedOrder_ShouldNotCancel() {
        // Arrange
        order.setStatus(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.SHIPPED, order.getStatus()); // Status should remain SHIPPED

        verify(orderRepository, never()).save(order);
        verify(productService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    public void testCancelDeliveredOrder_ShouldNotCancel() {
        // Arrange
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.DELIVERED, order.getStatus()); // Status should remain DELIVERED

        verify(orderRepository, never()).save(order);
        verify(productService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    public void testCancelCompletedOrder_ShouldNotCancel() {
        // Arrange
        order.setStatus(OrderStatus.COMPLETED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.COMPLETED, order.getStatus()); // Status should remain COMPLETED

        verify(orderRepository, never()).save(order);
        verify(productService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    public void testCancelAlreadyCanceledOrder_ShouldNotCancel() {
        // Arrange
        order.setStatus(OrderStatus.CANCELED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.CANCELED, order.getStatus()); // Status should remain CANCELED

        verify(orderRepository, never()).save(order);
        verify(productService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    public void testCancelProcessingOrder_ShouldNotCancel() {
        // Arrange
        order.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.PROCESSING, order.getStatus()); // Status should remain PROCESSING

        verify(orderRepository, never()).save(order);
        verify(productService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    public void testCancelNonExistentOrder_ShouldNotThrowException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert - Should not throw any exception
        orderService.cancelOrder(999L);

        verify(orderRepository, never()).save(any(Order.class));
        verify(productService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    public void testCancelPendingOrderWithNullItems_ShouldOnlyChangeStatus() {
        // Arrange
        order.setItems(null);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository).save(order);
        verify(productService, never()).restoreStock(anyLong(), anyInt());
    }

    @Test
    public void testCancelPendingOrderWithEmptyItems_ShouldOnlyChangeStatus() {
        // Arrange
        order.setItems(java.util.List.of());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Act
        orderService.cancelOrder(1L);

        // Assert
        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository).save(order);
        verify(productService, never()).restoreStock(anyLong(), anyInt());
    }
}
