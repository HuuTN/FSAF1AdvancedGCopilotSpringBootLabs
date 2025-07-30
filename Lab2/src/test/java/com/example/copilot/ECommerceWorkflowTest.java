package com.example.copilot;

import com.example.copilot.config.TestSecurityConfig;
import com.example.copilot.core.dto.CreateOrderRequestDTO;
import com.example.copilot.core.dto.OrderDTO;
import com.example.copilot.core.entity.*;
import com.example.copilot.core.enums.OrderStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Transactional
class ECommerceWorkflowTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Test
    void testPlaceOrderWorkflow() throws Exception {
        // Arrange - Much cleaner with helper method!
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
        
        // Verify order was persisted
        List<Order> orders = orderRepository.findAll();
        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void testPlaceOrderWorkflow_InsufficientStock() throws Exception {
        // Arrange
        TestData data = setupInitialData();
        CreateOrderRequestDTO orderRequest = createOrderRequestWithHighQuantity(data, 1000);
        
        // Act & Assert
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Sets up initial test data including User, Category, and Product
     * This helper method extracts the long 'Arrange' block logic to improve readability
     * @return TestData containing all created entities
     */
    private TestData setupInitialData() {
        // Clean existing data
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create test user
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@workflow.com");
        user.setPassword("password123");
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user);
        
        // Create test category
        Category category = new Category();
        category.setName("Electronics");
        Category savedCategory = categoryRepository.save(category);
        
        // Create test product
        Product product = new Product();
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(1299.99);
        product.setStockQuantity(50);
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.save(product);
        
        return new TestData(savedUser, savedProduct, savedCategory);
    }
    
    /**
     * Creates a valid order request with the test data
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
     * Creates order request with high quantity for testing stock validation
     */
    private CreateOrderRequestDTO createOrderRequestWithHighQuantity(TestData data, int quantity) {
        CreateOrderRequestDTO orderRequest = new CreateOrderRequestDTO();
        orderRequest.setUserId(data.user().getId());
        
        CreateOrderRequestDTO.OrderItemRequest item = new CreateOrderRequestDTO.OrderItemRequest();
        item.setProductId(data.product().getId());
        item.setQuantity(quantity);
        
        orderRequest.setItems(List.of(item));
        return orderRequest;
    }
    
    /**
     * Test data container for holding created entities
     * This record pattern makes the test data management cleaner and type-safe
     */
    private record TestData(User user, Product product, Category category) {}
}
