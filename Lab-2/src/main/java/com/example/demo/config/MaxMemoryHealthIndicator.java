package com.example.demo.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom Health Indicator that monitors memory usage
 * Reports DOWN status if used memory exceeds 90% of max memory
 */
@Component
public class MaxMemoryHealthIndicator implements HealthIndicator {

    private static final double MEMORY_THRESHOLD = 0.9; // 90%
    
    @Override
    public Health health() {
        Runtime runtime = Runtime.getRuntime();
        
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsagePercentage = (double) usedMemory / maxMemory;
        
        // Convert bytes to MB for better readability
        long maxMemoryMB = maxMemory / (1024 * 1024);
        long usedMemoryMB = usedMemory / (1024 * 1024);
        long freeMemoryMB = (maxMemory - usedMemory) / (1024 * 1024);
        
        Health.Builder healthBuilder;
        
        if (memoryUsagePercentage > MEMORY_THRESHOLD) {
            healthBuilder = Health.down()
                .withDetail("status", "Memory usage exceeded threshold")
                .withDetail("threshold", String.format("%.0f%%", MEMORY_THRESHOLD * 100));
        } else {
            healthBuilder = Health.up()
                .withDetail("status", "Memory usage is within acceptable limits");
        }
        
        return healthBuilder
            .withDetail("memoryUsagePercentage", String.format("%.2f%%", memoryUsagePercentage * 100))
            .withDetail("maxMemoryMB", maxMemoryMB)
            .withDetail("usedMemoryMB", usedMemoryMB)
            .withDetail("freeMemoryMB", freeMemoryMB)
            .withDetail("threshold", String.format("%.0f%%", MEMORY_THRESHOLD * 100))
            .withDetail("timestamp", java.time.LocalDateTime.now())
            .build();
    }
}
