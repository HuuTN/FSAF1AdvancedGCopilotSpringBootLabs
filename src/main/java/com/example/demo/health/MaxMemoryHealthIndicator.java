package com.example.demo.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MaxMemoryHealthIndicator implements HealthIndicator {

    private static final double MEMORY_THRESHOLD = 0.9; // 90%

    @Override
    public Health health() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        
        // Calculate used memory
        long usedMemory = totalMemory - freeMemory;
        
        // Calculate memory usage ratio
        double memoryUsageRatio = (double) usedMemory / maxMemory;
        
        Health.Builder healthBuilder = Health.up()
            .withDetail("total_memory", formatSize(totalMemory))
            .withDetail("free_memory", formatSize(freeMemory))
            .withDetail("used_memory", formatSize(usedMemory))
            .withDetail("max_memory", formatSize(maxMemory))
            .withDetail("memory_usage_ratio", String.format("%.2f%%", memoryUsageRatio * 100));

        if (memoryUsageRatio > MEMORY_THRESHOLD) {
            return healthBuilder
                .down()
                .withDetail("error", "Memory usage exceeds 90% threshold")
                .build();
        }

        return healthBuilder.build();
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
