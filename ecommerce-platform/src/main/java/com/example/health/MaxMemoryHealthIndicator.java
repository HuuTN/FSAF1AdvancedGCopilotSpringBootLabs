package com.example.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator that monitors memory usage.
 * Reports DOWN status if used memory exceeds 90% of max memory.
 */
@Component
public class MaxMemoryHealthIndicator implements HealthIndicator {

    private static final double THRESHOLD_PERCENTAGE = 0.90;

    @Override
    public Health health() {
        Runtime runtime = Runtime.getRuntime();

        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        double usedPercentage = (double) usedMemory / maxMemory;

        Health.Builder healthBuilder = usedPercentage < THRESHOLD_PERCENTAGE
                ? Health.up()
                : Health.down();

        return healthBuilder
                .withDetail("maxMemory", formatBytes(maxMemory))
                .withDetail("totalMemory", formatBytes(totalMemory))
                .withDetail("usedMemory", formatBytes(usedMemory))
                .withDetail("freeMemory", formatBytes(freeMemory))
                .withDetail("usedPercentage", String.format("%.2f%%", usedPercentage * 100))
                .withDetail("threshold", String.format("%.0f%%", THRESHOLD_PERCENTAGE * 100))
                .withDetail("status", usedPercentage < THRESHOLD_PERCENTAGE ? "HEALTHY" : "MEMORY_USAGE_HIGH")
                .build();
    }

    /**
     * Format bytes to human-readable format
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String[] units = { "B", "KB", "MB", "GB", "TB" };
        return String.format("%.2f %s", bytes / Math.pow(1024, exp), units[exp]);
    }
}
