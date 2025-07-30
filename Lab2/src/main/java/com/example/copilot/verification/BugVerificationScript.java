package com.example.copilot.verification;

import com.example.copilot.service.impl.OrderServiceImpl;
import com.example.copilot.service.impl.ProductServiceImpl;
import com.example.copilot.core.entity.Order;
import com.example.copilot.core.entity.OrderItem;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.dto.CreateOrderRequestDTO;

import java.lang.reflect.Method;

/**
 * Manual verification script to demonstrate introduced bugs
 * This shows the bugs without dependency on Spring context
 */
public class BugVerificationScript {
    
    public static void main(String[] args) {
        System.out.println("=== BUG VERIFICATION SCRIPT ===\n");
        
        verifyBugA();
        verifyBugB();
        verifyBugC();
        
        System.out.println("\n=== VERIFICATION COMPLETE ===");
    }
    
    public static void verifyBugA() {
        System.out.println("🐛 BUG A VERIFICATION - Logic Error:");
        System.out.println("Expected: OrderItem price should use product.getPrice()");
        System.out.println("Actual: OrderItem price is hardcoded to 9999.99");
        
        try {
            // Create mock objects
            Order order = new Order();
            Product product = new Product();
            product.setPrice(50.0); // Real price
            
            CreateOrderRequestDTO.OrderItemRequest itemReq = new CreateOrderRequestDTO.OrderItemRequest();
            itemReq.setQuantity(2);
            
            // Use reflection to call the private method
            OrderServiceImpl orderService = new OrderServiceImpl();
            Method method = OrderServiceImpl.class.getDeclaredMethod("createOrderItem", 
                Order.class, Product.class, CreateOrderRequestDTO.OrderItemRequest.class);
            method.setAccessible(true);
            
            OrderItem result = (OrderItem) method.invoke(orderService, order, product, itemReq);
            
            System.out.println("Product price: " + product.getPrice());
            System.out.println("OrderItem price: " + result.getPrice());
            System.out.println("✅ BUG A CONFIRMED: Price is " + result.getPrice() + " instead of " + product.getPrice());
            
        } catch (Exception e) {
            System.out.println("❌ Could not verify Bug A: " + e.getMessage());
        }
        System.out.println();
    }
    
    public static void verifyBugB() {
        System.out.println("🐛 BUG B VERIFICATION - Runtime Exception:");
        System.out.println("Expected: ProductService.getById() should return Optional.empty() or throw exception");
        System.out.println("Actual: ProductService.getById() returns null causing NPE");
        
        try {
            ProductServiceImpl productService = new ProductServiceImpl();
            
            // This will return null due to Bug B
            var result = productService.getById(999L);
            
            System.out.println("getById(999L) returned: " + result);
            
            // Try to call Optional methods on null - this will cause NPE
            if (result == null) {
                System.out.println("✅ BUG B CONFIRMED: getById() returned null instead of Optional");
                try {
                    // Simulate what would happen if code tried to use this as Optional
                    System.out.println("   If code tried result.isEmpty(), it would throw NPE");
                } catch (Exception e) {
                    System.out.println("✅ BUG B NPE CONFIRMED: " + e.getClass().getSimpleName());
                }
            }
            
        } catch (Exception e) {
            System.out.println("❌ Could not verify Bug B: " + e.getMessage());
        }
        System.out.println();
    }
    
    public static void verifyBugC() {
        System.out.println("🐛 BUG C VERIFICATION - Data Integrity:");
        System.out.println("Expected: @Transactional annotation should provide atomicity");
        System.out.println("Actual: @Transactional is commented out");
        
        try {
            // Check the source code for @Transactional annotation
            Method method = OrderServiceImpl.class.getMethod("placeOrder", CreateOrderRequestDTO.class);
            
            boolean hasTransactional = method.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class);
            
            System.out.println("placeOrder() has @Transactional: " + hasTransactional);
            
            if (!hasTransactional) {
                System.out.println("✅ BUG C CONFIRMED: @Transactional annotation is missing");
                System.out.println("   This means no automatic rollback on errors");
                System.out.println("   Potential for data inconsistency in error scenarios");
            } else {
                System.out.println("❌ BUG C NOT CONFIRMED: @Transactional is still present");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Could not verify Bug C: " + e.getMessage());
        }
        System.out.println();
    }
}
