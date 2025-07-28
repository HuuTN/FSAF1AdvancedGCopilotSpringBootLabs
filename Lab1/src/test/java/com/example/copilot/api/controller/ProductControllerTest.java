package com.example.copilot.api.controller;

import com.example.copilot.core.dto.ProductDTO;
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
class ProductControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void whenGetProductByIdNotFound_thenThrowException() throws Exception {
        mockMvc.perform(get("/api/products/999"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void whenDeleteProductNotFound_thenThrowException() throws Exception {
        mockMvc.perform(delete("/api/products/999"))
            .andExpect(status().isInternalServerError());
    }
}
