package com.example.usermanagement.controller;

import com.example.usermanagement.constant.OrderStatus;
import com.example.usermanagement.dto.OrderPostRequest;
import com.example.usermanagement.dto.OrderPostResponse;
import com.example.usermanagement.dto.OrderItemRequest;
import com.example.usermanagement.entity.Order;
import com.example.usermanagement.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;
    @Autowired
    private ObjectMapper objectMapper;

    private OrderPostRequest request;
    private Order order;

    @BeforeEach
    void setUp() {
        request = new OrderPostRequest();
        request.setUserId(1L);
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        request.setItems(java.util.List.of(item));
        order = Order.builder().id(10L).status(OrderStatus.CREATED).build();
    }

    @Test
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(any())).thenReturn(order);
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(10L))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }
} 