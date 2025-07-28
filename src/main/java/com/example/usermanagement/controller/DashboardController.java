package com.example.usermanagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1/dashboards")
@Tag(name = "Dashboard API", description = "API for dashboard management")
public class DashboardController {
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test")
    public String testDashboard() {
        return "This is Dashboard";
    }
}
