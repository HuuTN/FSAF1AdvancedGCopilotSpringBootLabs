package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.entity.Product;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ECommerceWorkflowTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    // A full end-to-end test for placing an order. This will involve creating a user and product via API calls, then placing the order.
    @Test
    void testPlaceOrderWorkflow() throws Exception {
        // Arrange: setup initial data
        TestEntities entities = setupInitialData();
        String userId = entities.userId;
        String productId = entities.productId;

        // Act: place order
        String orderJson = "{" +
                "\"userId\": " + userId + "," +
                "\"items\": [{" +
                "\"productId\": " + productId + "," +
                "\"quantity\": 2" +
                "}]" +
                "}";
        MvcResult orderResult = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderJson))
                .andExpect(status().isCreated())
                .andReturn();
        String orderId = extractIdFromResponse(orderResult);

        // Assert: verify order exists
        mockMvc.perform(get("/api/v1/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.items[0].product.id").value(productId))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    // Helper to setup initial test data
    private TestEntities setupInitialData() throws Exception {
        // Create user
        String userJson = "{" +
                "\"name\": \"Test User\"," +
                "\"email\": \"testuser@example.com\"," +
                "\"password\": \"password123\"}";
        MvcResult userResult = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andReturn();
        String userId = extractIdFromResponse(userResult);

        // Create product
        String productJson = "{" +
                "\"name\": \"Test Product\"," +
                "\"description\": \"A product for testing\"," +
                "\"price\": 99.99," +
                "\"stockQuantity\": 10" +
                "}";
        MvcResult productResult = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andReturn();
        String productId = extractIdFromResponse(productResult);

        return new TestEntities(userId, productId);
    }

    // Wrapper for test entity IDs
    private static class TestEntities {
        final String userId;
        final String productId;
        TestEntities(String userId, String productId) {
            this.userId = userId;
            this.productId = productId;
        }
    }

    // Helper to extract ID from response JSON
    private String extractIdFromResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        // Assumes response contains { "id": ... }
        int idIndex = content.indexOf("\"id\":");
        if (idIndex == -1) throw new RuntimeException("ID not found in response");
        int start = content.indexOf(":", idIndex) + 1;
        int end = content.indexOf(",", start);
        if (end == -1) end = content.indexOf("}", start);
        return content.substring(start, end).trim();
    }
}
