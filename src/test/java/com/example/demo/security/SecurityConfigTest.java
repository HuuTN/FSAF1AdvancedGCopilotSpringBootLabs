package com.example.demo.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    public void userShouldNotAccessDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void adminShouldAccessDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAllowPublicEndpoints() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
                
        mockMvc.perform(get("/api/products/1/reviews"))
                .andExpect(status().isOk());
    }
}
