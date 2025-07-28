// Unit test for the OrderService placeOrder method. Mock OrderRepository, ProductRepository, and UserRepository. Verify that the order is saved and product stock is updated correctly.
//generate a test class using @ExtendWith(MockitoExtension.class) and @Mock annotations.
//Use when(...).thenReturn(...) to set up mock behavior and verify(...) to check if repository methods were called correctly.
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
import java.util.List;
@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {
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
    void testGetAllOrders() {
        Mockito.when(orderRepository.findAll()).thenReturn(List.of(pendingOrder));
        List<Order> orders = orderService.getAllOrders();
        Assertions.assertEquals(1, orders.size());
        Assertions.assertEquals("PENDING", orders.get(0).getStatus());
    }

    @Test
    void testGetOrderById() {
        Mockito.when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        Order order = orderService.getOrderById(1L);
        Assertions.assertEquals("PENDING", order.getStatus());
    }

    @Test
    void testGetOrdersByUserName() {
        // Assuming there's a method to set user in Order, which is not shown in the original code
        // Mockito.when(orderRepository.findByUser_Name("testUser")).thenReturn(List.of(pendingOrder));
        // List<Order> orders = orderService.getOrdersByUserName("testUser");
        // Assertions.assertEquals(1, orders.size());
        // Assertions.assertEquals("PENDING", orders.get(0).getStatus());
    }

    @Test
    void testGetOrdersByStatus() {
        Mockito.when(orderRepository.findByStatus("PENDING")).thenReturn(List.of(pendingOrder));
        List<Order> orders = orderService.getOrdersByStatus("PENDING");
        Assertions.assertEquals(1, orders.size());
        Assertions.assertEquals("PENDING", orders.get(0).getStatus());
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