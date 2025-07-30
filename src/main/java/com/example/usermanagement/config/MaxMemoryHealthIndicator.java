package com.example.usermanagement.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MaxMemoryHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;

        double usedMemoryPercentage = ((double) usedMemory / maxMemory) * 100;

        if (usedMemoryPercentage > 90) {
            return Health.down()
                    .withDetail("error", "Memory usage exceeds 90% of max memory")
                    .withDetail("usedMemory", usedMemory)
                    .withDetail("maxMemory", maxMemory)
                    .withDetail("usedMemoryPercentage", usedMemoryPercentage)
                    .build();
        }

        return Health.up()
                .withDetail("usedMemory", usedMemory)
                .withDetail("maxMemory", maxMemory)
                .withDetail("usedMemoryPercentage", usedMemoryPercentage)
                .build();
    }
}