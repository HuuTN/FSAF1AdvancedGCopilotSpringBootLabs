package com.example.demo.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MaxMemoryHealthIndicator implements HealthIndicator {

    private static final double MAX_MEMORY_THRESHOLD = 0.9; // 90%

    @Override
    public Health health() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        double memoryUsageRatio = (double) usedMemory / maxMemory;
        
        Health.Builder status = memoryUsageRatio < MAX_MEMORY_THRESHOLD ? Health.up() : Health.down();
        
        return status
            .withDetail("max_memory", formatSize(maxMemory))
            .withDetail("total_memory", formatSize(totalMemory))
            .withDetail("free_memory", formatSize(freeMemory))
            .withDetail("used_memory", formatSize(usedMemory))
            .withDetail("memory_usage_ratio", String.format("%.2f%%", memoryUsageRatio * 100))
            .build();
    }
    
    private String formatSize(long bytes) {
        long kilobytes = bytes / 1024;
        long megabytes = kilobytes / 1024;
        return String.format("%d MB", megabytes);
    }
}
