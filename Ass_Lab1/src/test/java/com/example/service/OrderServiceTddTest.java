// Write a test method to cancel a PENDING order. It should verify the order status becomes CANCELLED and product stock is restored. Your code will not compile if the OrderService is not implemented correctly.
//Implement the logic inside cancelOrder to find the order, update its status, and update the stock of all associated products. The test should also handle cases where the order is not found or cannot be cancelled due to its status.
package com.example.service;
import com.example.entity.Order;
import com.example.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.Optional;
@ExtendWith(MockitoExtension.class)
public class OrderServiceTddTest {
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order pendingOrder;

    @BeforeEach
    void setUp() {
        pendingOrder = new Order();
        pendingOrder.setId(1L);
        pendingOrder.setStatus("PENDING");
        // Assuming there's a method to set product and user, which is not shown in the original code
        // pendingOrder.setProduct(product);
        // pendingOrder.setUser(user);
    }

    @Test
    void testCancelPendingOrder() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        
        orderService.cancelOrder(1L);
        
        Assertions.assertEquals("CANCELLED", pendingOrder.getStatus());
        Mockito.verify(orderRepository).save(pendingOrder);
        // Verify that product stock restoration logic is called, if applicable
        // Mockito.verify(productRepository).restoreStock(pendingOrder.getProduct().getId());
    }
    @Test
    void testCancelOrderNotFound() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            orderService.cancelOrder(1L);
        });
        Assertions.assertEquals("Order not found with id: 1", exception.getMessage());
    }

    @Test
    void testCancelNonPendingOrder() {
        pendingOrder.setStatus("COMPLETED");
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> {
            orderService.cancelOrder(1L);
        });
        Assertions.assertEquals("Order cannot be cancelled as it is not in PENDING status", exception.getMessage());
    }
}
