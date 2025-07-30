package com.example.controller;

import com.example.annotation.RateLimited;
import com.example.model.dto.DashboardStatsDTO;
import com.example.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for dashboard operations.
 * Provides endpoints for retrieving dashboard statistics and metrics.
 * Access restricted to ADMIN and MANAGER roles only.
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Dashboard statistics and metrics API - Admin/Manager access only")
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Get comprehensive dashboard statistics.
     * Requires ADMIN or MANAGER role access.
     * Rate limited to 10 requests per minute per client.
     * 
     * @return DashboardStatsDTO containing total revenue, total orders, and new
     *         customers this month
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @RateLimited(requests = 10, period = "1m")
    @Operation(summary = "Get dashboard statistics", description = "Retrieve comprehensive dashboard statistics including total revenue from delivered orders, total order count, and new customers this month. Requires ADMIN or MANAGER role.", security = @SecurityRequirement(name = "basicAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard statistics retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges (ADMIN or MANAGER role required)"),
            @ApiResponse(responseCode = "429", description = "Too Many Requests - Rate limit exceeded (10 requests per minute)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        try {
            logger.info("Dashboard statistics requested");
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            logger.info("Dashboard statistics retrieved successfully");
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error retrieving dashboard statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
