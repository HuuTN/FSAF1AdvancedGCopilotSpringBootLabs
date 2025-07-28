package com.example.copilot.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private DataSource dataSource;

    @BeforeEach
    void setup() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            // Xóa order trước để tránh lỗi khóa ngoại
            conn.prepareStatement("DELETE FROM order_item").execute();
            conn.prepareStatement("DELETE FROM orders").execute();
            conn.prepareStatement("DELETE FROM user").execute();
            conn.prepareStatement("INSERT INTO user (id, name, email, password, role) VALUES (1, 'John Doe', 'john@example.com', '123', 'USER')").execute();
        }
    }

    @Test
    void whenGetUserById_thenReturnUserDTO() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void whenGetUserByIdNotFound_thenThrowException() throws Exception {
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isInternalServerError());
    }
}
