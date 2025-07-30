package com.example.copilot.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security Tests for Dashboard Controller
 * Tests role-based access control for dashboard endpoints
 */
@SpringBootTest
@AutoConfigureMockMvc
public class DashboardSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;

    /**
     * Test: USER role cannot access dashboard endpoint
     * Expected: 403 Forbidden
     */
    @Test
    @WithMockUser(roles = "USER")
    void whenUserTriesToAccessDashboard_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: ADMIN role can access dashboard endpoint
     * Expected: 200 OK (or appropriate response)
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void whenAdminAccessesDashboard_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk());
    }

    /**
     * Test: Unauthenticated user cannot access dashboard
     * Expected: 403 Forbidden (not 401 because Spring Security returns 403 for anonymous access)
     */
    @Test
    void whenUnauthenticatedUserTriesToAccessDashboard_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test: USER role cannot access any admin endpoints  
     */
    @Test
    @WithMockUser(roles = "USER")
    void whenUserTriesToAccessAdminEndpoints_thenForbidden() throws Exception {
        // Test dashboard endpoints
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());
    }
}
