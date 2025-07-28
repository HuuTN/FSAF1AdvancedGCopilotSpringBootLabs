package com.example.copilot.api.controller;

import com.example.copilot.core.dto.CategoryDTO;
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
class CategoryControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void whenGetCategoryByIdNotFound_thenThrowException() throws Exception {
        mockMvc.perform(get("/api/categories/999"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void whenDeleteCategoryNotFound_thenThrowException() throws Exception {
        mockMvc.perform(delete("/api/categories/999"))
            .andExpect(status().isInternalServerError());
    }
}
