package com.example.usermanagement;

import com.example.usermanagement.dto.OrderItemRequest;
import com.example.usermanagement.dto.OrderPostRequest;
import com.example.usermanagement.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.usermanagement.entity.Category;
import com.example.usermanagement.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class ECommerceWorkflowTest extends PostgresTestContainerConfig {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void fullOrderWorkflow() throws Exception {
        // 1. Insert category into DB
        Category category = Category.builder().name("Workflow Category").build();
        category = categoryRepository.save(category);

        // 2. Create user
        UserDTO userDTO = UserDTO.builder().name("Workflow User").email("workflow@example.com").build();
        MvcResult userResult = mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        UserDTO createdUser = objectMapper.readValue(userResult.getResponse().getContentAsString(), UserDTO.class);
        assertThat(createdUser.getId()).isNotNull();

        // 3. Create product (set categoryId)
        String productJson = "{" +
                "\"name\":\"Workflow Product\"," +
                "\"price\":100," +
                "\"stock\":5," +
                "\"categoryId\":" + category.getId() +
                "}";
        MvcResult productResult = mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        Long productId = objectMapper.readTree(productResult.getResponse().getContentAsString()).get("id").asLong();

        // 4. Create order
        OrderPostRequest orderRequest = new OrderPostRequest();
        orderRequest.setUserId(createdUser.getId());
        OrderItemRequest orderItemRequest = new OrderItemRequest();

        orderItemRequest.setProductId(productId);
        orderItemRequest.setQuantity(2);
        orderRequest.setItems(List.of(orderItemRequest));
        MvcResult orderResult = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andReturn();
        Long orderId = objectMapper.readTree(orderResult.getResponse().getContentAsString()).get("orderId").asLong();

        // 5. Check stock after order created
        MvcResult productAfterOrder = mockMvc.perform(get("/api/v1/products/" + productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        int stockAfterOrder = objectMapper.readTree(productAfterOrder.getResponse().getContentAsString()).get("stock").asInt();
        assertThat(stockAfterOrder).isEqualTo(3);

        // 6. Cancel order
        // mockMvc.perform(post("/api/v1/orders/" + orderId + "/cancel"))
        //         .andExpect(status().isOk());
    }
} 