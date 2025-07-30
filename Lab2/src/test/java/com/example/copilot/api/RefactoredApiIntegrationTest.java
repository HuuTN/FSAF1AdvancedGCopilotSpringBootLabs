package com.example.copilot.api;

import com.example.copilot.config.TestSecurityConfig;
import com.example.copilot.core.dto.*;
import com.example.copilot.core.entity.*;
import com.example.copilot.core.enums.UserRole;
import com.example.copilot.core.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Refactored integration test demonstrating Task 5: Interpreting Quality Metrics
 * This version shows how extracting setup logic into helper methods improves readability
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Transactional
public class RefactoredApiIntegrationTest {
    
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private OrderRepository orderRepository;

    @Test
    void testCompleteECommerceWorkflow() throws Exception {
        // Arrange - Much cleaner with helper method!
        TestData data = setupInitialData();
        
        // Act & Assert - Test complete workflow
        testUserOperations(data);
        testProductOperations(data);
        testOrderOperations(data);
    }

    @Test
    void testProductSearchWorkflow() throws Exception {
        // Arrange
        TestData data = setupInitialData();
        
        // Act - Search for products
        mockMvc.perform(get("/api/products?keyword=" + data.product().getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(data.product().getName()));
    }

    @Test
    void testOrderPlacementWorkflow() throws Exception {
        // Arrange
        TestData data = setupInitialData();
        CreateOrderRequestDTO orderRequest = createOrderRequest(data);
        
        // Act
        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();
        
        // Assert
        OrderDTO orderResponse = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            OrderDTO.class
        );
        
        assertThat(orderResponse.getId()).isNotNull();
        assertThat(orderResponse.getUserId()).isEqualTo(data.user().getId());
    }

    /**
     * Sets up initial test data including User, Category, and Product
     * This helper method extracts the long 'Arrange' block logic to improve readability
     * @return TestData containing all created entities so the test method can use them
     */
    private TestData setupInitialData() {
        // Clean existing data to ensure test isolation
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create test category
        Category category = new Category();
        category.setName("Test Electronics");
        Category savedCategory = categoryRepository.save(category);
        
        // Create test user
        User user = new User();
        user.setName("Test Customer");
        user.setEmail("customer@test.com");
        user.setPassword("securepass");
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user);
        
        // Create test product
        Product product = new Product();
        product.setName("Test Smartphone");
        product.setDescription("High-end smartphone for testing");
        product.setPrice(899.99);
        product.setStockQuantity(25);
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.save(product);
        
        return new TestData(savedUser, savedProduct, savedCategory);
    }
    
    /**
     * Helper method to test user-related operations
     */
    private void testUserOperations(TestData data) throws Exception {
        mockMvc.perform(get("/api/users/" + data.user().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(data.user().getName()));
    }
    
    /**
     * Helper method to test product-related operations
     */
    private void testProductOperations(TestData data) throws Exception {
        mockMvc.perform(get("/api/products/" + data.product().getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(data.product().getName()));
    }
    
    /**
     * Helper method to test order-related operations
     */
    private void testOrderOperations(TestData data) throws Exception {
        CreateOrderRequestDTO orderRequest = createOrderRequest(data);
        
        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andReturn();
                
        OrderDTO orderResponse = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            OrderDTO.class
        );
        
        // Test getting the created order
        mockMvc.perform(get("/api/orders/" + orderResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderResponse.getId()));
    }
    
    /**
     * Creates a valid order request using the test data
     */
    private CreateOrderRequestDTO createOrderRequest(TestData data) {
        CreateOrderRequestDTO orderRequest = new CreateOrderRequestDTO();
        orderRequest.setUserId(data.user().getId());
        
        CreateOrderRequestDTO.OrderItemRequest item = new CreateOrderRequestDTO.OrderItemRequest();
        item.setProductId(data.product().getId());
        item.setQuantity(2);
        
        orderRequest.setItems(List.of(item));
        return orderRequest;
    }
    
    /**
     * Test data container for holding created entities
     * This record pattern makes the test data management cleaner and type-safe
     */
    private record TestData(User user, Product product, Category category) {}
}
