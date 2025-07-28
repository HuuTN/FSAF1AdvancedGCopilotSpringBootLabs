package com.example.demo.apis.controller;

import com.example.demo.core.dtos.DashboardStatsDTO;
import com.example.demo.services.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Dashboard Statistics
 * Provides aggregated statistics for admin dashboard
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private OrderService orderService;

    /**
     * Get dashboard statistics including total revenue, total orders, and new customers this month
     * 
     * @return DashboardStatsDTO containing aggregated statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        try {
            DashboardStatsDTO stats = orderService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // Log error and return appropriate response
            return ResponseEntity.internalServerError().build();
        }
    }
}
