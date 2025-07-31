package com.example.usermanagement.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MaxMemoryHealthIndicator implements HealthIndicator {

    private static final double THRESHOLD = 0.9; // 90%

    @Override
    public Health health() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double usage = (double) usedMemory / maxMemory;

        if (usage > THRESHOLD) {
            return Health.down()
                    .withDetail("error", "Used memory exceeds 90% of max memory")
                    .withDetail("usedMemory", usedMemory)
                    .withDetail("maxMemory", maxMemory)
                    .withDetail("usage", String.format("%.2f%%", usage * 100))
                    .build();
        } else {
            return Health.up()
                    .withDetail("usedMemory", usedMemory)
                    .withDetail("maxMemory", maxMemory)
                    .withDetail("usage", String.format("%.2f%%", usage * 100))
                    .build();
        }
    }
} 