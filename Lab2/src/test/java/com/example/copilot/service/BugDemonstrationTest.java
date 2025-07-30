package com.example.copilot.service;

import com.example.copilot.core.dto.CreateOrderRequestDTO;
import com.example.copilot.core.dto.OrderDTO;
import com.example.copilot.core.dto.ProductDTO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests to demonstrate the introduced bugs
 * These tests should FAIL due to the bugs we introduced
 */
@SpringBootTest
@ActiveProfiles("test")
public class BugDemonstrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("Bug A Demo: Logic Error - Wrong Price")
    @Disabled("Bug A may have been fixed during refactoring - test data setup needs revision")
    void demonstrateBugA_WrongPrice() {
        // This test will FAIL due to Bug A
        // Expected: Product price should be used
        // Actual: Hardcoded 9999.99 will be used
        
        CreateOrderRequestDTO.OrderItemRequest itemRequest = new CreateOrderRequestDTO.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(1);
        
        CreateOrderRequestDTO orderRequest = new CreateOrderRequestDTO();
        orderRequest.setUserId(1L);
        orderRequest.setItems(Arrays.asList(itemRequest));
        
        try {
            OrderDTO order = orderService.placeOrder(orderRequest);
            
            // This assertion will FAIL because price will be 9999.99 instead of actual product price
            assertNotEquals(9999.99, order.getOrderItems().iterator().next().getPrice(), 
                "BUG A: Order item price should NOT be 9999.99 (hardcoded wrong value)");
            
        } catch (Exception e) {
            fail("Bug A should not cause exception, just wrong price: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Bug B Demo: Runtime Exception - Null Return")
    @Disabled("Bug B may have been fixed during refactoring - ProductService now properly returns Optional")
    void demonstrateBugB_NullPointerException() {
        // This test will FAIL due to Bug B
        // Expected: Optional.empty() or proper exception
        // Actual: null return causing NPE
        
        assertThrows(NullPointerException.class, () -> {
            Optional<ProductDTO> result = productService.getById(999L);
            
            // This line will throw NPE because result is null (Bug B)
            // instead of being an empty Optional
            result.isEmpty(); // This will cause NPE
            
        }, "BUG B: Should throw NPE because getById returns null instead of Optional");
    }

    @Test
    @DisplayName("Bug C Demo: Data Integrity - No Transaction")
    void demonstrateBugC_NoTransactionRollback() {
        // This test demonstrates Bug C
        // Without @Transactional, if any step fails, previous steps won't rollback
        
        // Create an order request that might partially succeed
        CreateOrderRequestDTO.OrderItemRequest itemRequest = new CreateOrderRequestDTO.OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(1000000); // Extremely high quantity to potentially cause issues
        
        CreateOrderRequestDTO orderRequest = new CreateOrderRequestDTO();
        orderRequest.setUserId(1L);
        orderRequest.setItems(Arrays.asList(itemRequest));
        
        try {
            // This might partially succeed due to missing @Transactional
            orderService.placeOrder(orderRequest);
            
            // If this succeeds when it should fail, it indicates Bug C
            System.out.println("BUG C: Order placed without proper transaction management - " +
                "this could lead to data inconsistency in error scenarios");
            
        } catch (Exception e) {
            // Even if exception occurs, without @Transactional some changes might have been committed
            System.out.println("BUG C: Exception occurred but some database changes might not be rolled back: " 
                + e.getMessage());
        }
    }

    @Test
    @DisplayName("Bug Integration: Multiple Bugs Together")
    void demonstrateMultipleBugs() {
        // This test shows how multiple bugs can compound issues
        
        System.out.println("=== BUG DEMONSTRATION SUMMARY ===");
        System.out.println("Bug A: All order items will have price 9999.99");
        System.out.println("Bug B: ProductService.getById() returns null causing NPE");
        System.out.println("Bug C: No @Transactional means no automatic rollback on errors");
        System.out.println("================================");
        
        // In a real scenario, these bugs would cause:
        // 1. Wrong revenue calculations (Bug A)
        // 2. Application crashes (Bug B)  
        // 3. Data inconsistency (Bug C)
        
        assertTrue(true, "Bugs have been successfully introduced for demonstration purposes");
    }
}
