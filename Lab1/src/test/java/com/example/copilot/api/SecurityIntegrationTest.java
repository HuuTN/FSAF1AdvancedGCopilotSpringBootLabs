package com.example.copilot.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security Integration Tests
 * Tests the security configuration for Dashboard API
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(locations = "classpath:application-security-test.properties")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getDashboardStats_WithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void getDashboardStats_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getDashboardStats_WithAdminRole_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.totalRevenue").exists())
                .andExpect(jsonPath("$.totalOrders").exists())
                .andExpect(jsonPath("$.newCustomersThisMonth").exists());
    }

    @Test
    public void getProductReviews_ShouldBePublic() throws Exception {
        // This endpoint should be accessible without authentication
        mockMvc.perform(get("/api/reviews/product/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void createReview_WithAuthentication_ShouldBeAccessible() throws Exception {
        // This endpoint should be accessible with USER role
        // Note: This will fail due to validation but should not return 401/403
        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isMethodNotAllowed()); // GET not allowed, but no auth error
    }
}
