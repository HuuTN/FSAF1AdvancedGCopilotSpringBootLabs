package com.example.controller;

import com.example.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

import com.example.model.dto.DashboardStatsDTO;
import org.junit.jupiter.api.BeforeEach;
import java.math.BigDecimal;

/**
 * Security tests for DashboardController to verify role-based access control.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext
class DashboardControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        // Mock the service to return a valid response
        DashboardStatsDTO mockStats = new DashboardStatsDTO();
        mockStats.setTotalRevenue(BigDecimal.valueOf(10000));
        mockStats.setTotalOrders(100L);
        mockStats.setNewCustomersThisMonth(25L);
        
        when(dashboardService.getDashboardStats()).thenReturn(mockStats);
    }

    @Test
    void getDashboardStats_WithoutAuthentication_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/stats"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDashboardStats_WithUserRole_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboardStats_WithAdminRole_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/stats"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void getDashboardStats_WithManagerRole_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/stats"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = { "ADMIN", "MANAGER" })
    void getDashboardStats_WithMultipleValidRoles_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard/stats"))
                .andExpect(status().isOk());
    }
}
