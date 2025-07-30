package com.example.controller;

import com.example.dto.DashboardStatsDTO;
import com.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Dashboard Controller - Contains sensitive business data
 * TODO: Add proper authentication and authorization
 * Should only be accessible by ADMIN users
 */
@RestController
@RequestMapping("/api/dashboard")
// TODO: Add @PreAuthorize("hasRole('ADMIN')") when security is configured
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get dashboard statistics
     * WARNING: This endpoint exposes sensitive business data and should be secured
     */
    @GetMapping("/stats")
    // TODO: Add @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDashboardStats() {
        try {
            DashboardStatsDTO stats = dashboardService.getDashboardStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Internal Error", "Failed to retrieve dashboard statistics"));
        }
    }
    
    private Map<String, String> createErrorResponse(String error, String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        return errorResponse;
    }
}
