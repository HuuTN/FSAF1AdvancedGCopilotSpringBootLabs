package com.example.demo.controller;

import com.example.demo.dto.DashboardDataDTO;
import com.example.demo.service.DashboardService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/stats")
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    @GetMapping
    public ResponseEntity<DashboardDataDTO> getDashboardData(
        @RequestParam(defaultValue = "5") @Min(1) @Max(20) int topProductsLimit,
        @RequestParam(defaultValue = "10") @Min(1) @Max(50) int recentOrdersLimit
    ) {
        return ResponseEntity.ok(dashboardService.getDetailedDashboardData(topProductsLimit, recentOrdersLimit));
    }
}
