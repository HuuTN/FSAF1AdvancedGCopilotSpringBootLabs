package com.example.copilot.controller;

import com.example.copilot.config.MaxMemoryHealthIndicator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health controller to expose custom health checks including memory monitoring.
 * This supplements the Spring Boot Actuator health endpoint.
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final MaxMemoryHealthIndicator memoryHealthIndicator;

    /**
     * Custom memory health endpoint that shows detailed memory usage information.
     * Returns DOWN status if memory usage exceeds 90% threshold.
     */
    @GetMapping("/memory")
    public ResponseEntity<Map<String, Object>> getMemoryHealth() {
        MaxMemoryHealthIndicator.MemoryHealthStatus memoryStatus = memoryHealthIndicator.checkMemoryHealth();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", memoryStatus.getStatus());
        response.put("message", memoryStatus.getMessage());
        
        Map<String, Object> details = new HashMap<>();
        details.put("maxMemory", memoryStatus.getMaxMemory());
        details.put("totalMemory", memoryStatus.getTotalMemory());
        details.put("usedMemory", memoryStatus.getUsedMemory());
        details.put("freeMemory", memoryStatus.getFreeMemory());
        details.put("memoryUsagePercentage", memoryStatus.getMemoryUsagePercentage());
        details.put("threshold", memoryStatus.getThreshold());
        
        response.put("details", details);
        
        // Return appropriate HTTP status based on health check
        if ("DOWN".equals(memoryStatus.getStatus())) {
            return ResponseEntity.status(503).body(response); // Service Unavailable
        } else {
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * Simplified health check endpoint that returns basic system information.
     */
    @GetMapping("/simple")
    public ResponseEntity<Map<String, Object>> getSimpleHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        response.put("application", "Copilot E-Commerce API");
        
        // Add JVM info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> jvmInfo = new HashMap<>();
        jvmInfo.put("availableProcessors", runtime.availableProcessors());
        jvmInfo.put("maxMemory", runtime.maxMemory());
        jvmInfo.put("totalMemory", runtime.totalMemory());
        jvmInfo.put("freeMemory", runtime.freeMemory());
        
        response.put("jvm", jvmInfo);
        
        return ResponseEntity.ok(response);
    }
}
