package com.example.copilot.monitoring;

import org.springframework.stereotype.Component;

/**
 * Custom Health Indicator for Memory Monitoring  
 * Reports DOWN status if memory usage exceeds 90%
 * Simplified implementation due to Spring Boot 3.x module issues
 */
@Component
public class MaxMemoryHealthIndicator {

    /**
     * Check memory health status
     * @return Map with health status and details
     */
    public java.util.Map<String, Object> health() {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        try {
            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long maxMemory = Runtime.getRuntime().maxMemory();
            double usedRatio = (double) usedMemory / maxMemory;
            
            // Core logic exactly as specified in task
            if (usedRatio > 0.9) {
                result.put("status", "DOWN");
                result.put("reason", "Exceeds 90% memory usage");
                result.put("usage", String.format("%.2f%%", usedRatio * 100));
            } else {
                result.put("status", "UP");
                result.put("usage", String.format("%.2f%%", usedRatio * 100));
                result.put("usedMemory", formatBytes(usedMemory));
                result.put("maxMemory", formatBytes(maxMemory));
            }
            
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", "Unable to check memory status");
        }
        
        return result;
    }
    
    /**
     * Get simple status for external monitoring
     */
    public String getMemoryStatus() {
        java.util.Map<String, Object> health = health();
        String status = (String) health.get("status");
        String usage = (String) health.get("usage");
        return String.format("Memory Status: %s (%s used)", status, usage != null ? usage : "N/A");
    }
    
    private String formatBytes(long bytes) {
        if (bytes >= 1024 * 1024 * 1024) {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        } else if (bytes >= 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f KB", bytes / 1024.0);
        }
    }
}
