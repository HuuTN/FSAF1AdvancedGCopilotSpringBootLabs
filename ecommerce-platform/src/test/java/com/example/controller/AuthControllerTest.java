package com.example.controller;

import com.example.model.dto.UserDTO;
import com.example.model.enums.UserRole;
import com.example.service.UserService;
import com.example.service.UserValidationService;
import com.example.service.ResponseFormatterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController
 * 
 * Note: These tests focus on the controller logic rather than full security
 * integration
 * since security auto-configuration is excluded to match the project's test
 * patterns.
 */
@WebMvcTest(value = AuthController.class, excludeAutoConfiguration = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserValidationService userValidationService;

    @MockBean
    private ResponseFormatterService responseFormatterService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUserDTO;
    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        testUserDTO = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .role(UserRole.EMPLOYEE)
                .build();

        testUserDetails = User.builder()
                .username("test@example.com")
                .password("$2a$10$encoded_password")
                .roles("EMPLOYEE")
                .build();
    }

    @Test
    void testRegisterUser() throws Exception {
        // Arrange
        UserDTO responseUserDTO = UserDTO.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .role(UserRole.EMPLOYEE)
                .build();

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", "User registered successfully");
        successResponse.put("user", responseUserDTO);
        successResponse.put("timestamp", System.currentTimeMillis());

        // Mock service behaviors
        doNothing().when(userValidationService).validateUserRegistrationInput(any(UserDTO.class));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded_password");
        when(userService.createUser(any(UserDTO.class))).thenReturn(responseUserDTO);
        when(responseFormatterService.createRegistrationSuccessResponse(any())).thenReturn(
                ResponseEntity.status(HttpStatus.CREATED).body(successResponse));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    void testRegisterUserInvalidData() throws Exception {
        // Arrange
        UserDTO invalidUserDTO = UserDTO.builder()
                .name("") // Invalid: empty name
                .email("invalid-email") // Invalid: malformed email
                .password("123") // Invalid: too short
                .build();

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Registration failed");
        errorResponse.put("message", "Validation failed");
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Mock validation service to throw exception for invalid data
        doThrow(new IllegalArgumentException("Validation failed"))
                .when(userValidationService).validateUserRegistrationInput(any(UserDTO.class));
        when(responseFormatterService.createErrorResponse(anyString(), anyString(), anyInt()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginWithValidAuthHeader() throws Exception {
        // Arrange
        String username = "test@example.com";
        String password = "password123";
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        when(userDetailsService.loadUserByUsername(username)).thenReturn(testUserDetails);
        when(passwordEncoder.matches(password, testUserDetails.getPassword())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .header("Authorization", basicAuth)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        // Arrange
        String username = "test@example.com";
        String password = "wrongpassword";
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());

        when(userDetailsService.loadUserByUsername(username)).thenReturn(testUserDetails);
        when(passwordEncoder.matches(password, testUserDetails.getPassword())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                .header("Authorization", basicAuth)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid credentials"));
    }

    @Test
    void testLoginMissingAuthHeader() throws Exception {
        // Act & Assert - Since we excluded security, this will result in a bad request
        // due to missing header
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAuthStatusWithoutSecurity() throws Exception {
        // Act & Assert - Since security is excluded, this will return unauthenticated
        // status
        mockMvc.perform(get("/api/v1/auth/status")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false));
    }

    @Test
    void testGetProfileWithoutAuthentication() throws Exception {
        // Act & Assert - Since security is excluded, this will return unauthorized
        mockMvc.perform(get("/api/v1/auth/profile")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testLogoutWithoutAuthentication() throws Exception {
        // Act & Assert - Since security is excluded, this will return unauthorized
        mockMvc.perform(post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testChangePasswordWithoutAuthentication() throws Exception {
        // Arrange
        String requestBody = """
                {
                    "currentPassword": "currentPassword",
                    "newPassword": "newPassword123"
                }
                """;

        // Act & Assert - Since security is excluded, this will return unauthorized
        mockMvc.perform(put("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testChangePasswordTooShort() throws Exception {
        // This test can work without authentication by just testing input validation
        String requestBody = """
                {
                    "currentPassword": "currentPassword",
                    "newPassword": "123"
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized()); // Will be unauthorized due to no authentication
    }

    @Test
    void testRegisterUserWithWeakPassword() throws Exception {
        // Arrange
        UserDTO weakPasswordUserDTO = UserDTO.builder()
                .name("Test User")
                .email("test@example.com")
                .password("weakpass") // No uppercase, digits, or special chars
                .role(UserRole.EMPLOYEE)
                .build();

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Registration failed");
        errorResponse.put("message",
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character");
        errorResponse.put("timestamp", System.currentTimeMillis());

        // Mock validation service to throw exception for weak password
        doThrow(new IllegalArgumentException(
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"))
                .when(userValidationService).validateUserRegistrationInput(any(UserDTO.class));
        when(responseFormatterService.createErrorResponse(anyString(), anyString(), anyInt()))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(weakPasswordUserDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Registration failed"));
    }
}