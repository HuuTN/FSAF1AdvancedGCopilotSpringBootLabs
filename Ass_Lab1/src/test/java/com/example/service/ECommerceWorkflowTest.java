//Annotate it with @SpringBootTest and @AutoConfigureMockMvc. This loads the entire application context.
// A full end-to-end test for placing an orderinfor. This will involve creating a user and product via API calls, then placing the order.
package com.example.service;
import com.example.dto.UserDTO;
import com.example.entity.Order;
import com.example.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@AutoConfigureMockMvc
public class ECommerceWorkflowTest {

    //Use MockMvc to perform a sequence of API calls: POST /api/v1/users, POST /api/v1/products, then POST /api/v1/orders.
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void testECommerceWorkflow() throws Exception {
        // Step 1: Create a user
        UserDTO userDTO = new UserDTO();
        userDTO.setName("Test User");
        userDTO.setEmail("testuser@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .contentType("application/json")
                .content("{\"name\":\"Test User\",\"email\":\"testuser@example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Step 2: Create a product
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
                .contentType("application/json")
                .content("{\"name\":\"Test Product\",\"price\":100.0}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Step 3: Place an order
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
                .contentType("application/json")
                .content("{\"userId\":1,\"productId\":1,\"quantity\":2}"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Assert: Verify the order was created in the database
        List<Order> orders = orderRepository.findAll();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo("PENDING");
    }
}