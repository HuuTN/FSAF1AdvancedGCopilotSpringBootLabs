package com.example.copilot.api.controller;

import com.example.copilot.core.dto.OrderDTO;
import com.example.copilot.core.dto.CreateOrderRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void whenGetOrderByIdNotFound_thenThrowException() throws Exception {
        mockMvc.perform(get("/api/orders/999"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void whenDeleteOrderNotFound_thenThrowException() throws Exception {
        mockMvc.perform(delete("/api/orders/999"))
            .andExpect(status().isInternalServerError());
    }
}
