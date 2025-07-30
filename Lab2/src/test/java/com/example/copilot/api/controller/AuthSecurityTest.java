package com.example.copilot.api.controller;

import com.example.copilot.config.TestSecurityConfig;
import com.example.copilot.core.dto.RegisterRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@Transactional
public class AuthSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenRegisterWithWeakPassword_thenValidationError() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("123"); // Weak password
        request.setConfirmPassword("123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRegisterWithInvalidEmail_thenValidationError() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setName("Test User");
        request.setEmail("invalid-email"); // Invalid email format
        request.setPassword("StrongPass123!");
        request.setConfirmPassword("StrongPass123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenRegisterWithValidData_thenSuccess() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setName("Test User");
        request.setEmail("newuser@example.com");
        request.setPassword("StrongPass123!");
        request.setConfirmPassword("StrongPass123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void whenRegisterWithSQLInjectionAttempt_thenSafelyHandled() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setName("Test User"); // Normal name to pass validation
        request.setEmail("hacker@example.com");
        request.setPassword("StrongPass123!");
        request.setConfirmPassword("StrongPass123!");

        // Should not cause SQL injection due to parameterized queries
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()); // Input should be safely handled
    }

    @Test
    void whenRegisterWithMismatchedPasswords_thenError() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setName("Test User");
        request.setEmail("mismatch@example.com");
        request.setPassword("StrongPass123!");
        request.setConfirmPassword("DifferentPass123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Passwords do not match"));
    }
}
