package com.example.demo.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MaxMemoryHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        double usage = (double) usedMemory / maxMemory;
        if (usage > 0.9) {
            return Health.down()
                .withDetail("error", "Used memory exceeds 90% of max memory")
                .withDetail("usedMemory", usedMemory)
                .withDetail("maxMemory", maxMemory)
                .build();
        }
        return Health.up()
            .withDetail("usedMemory", usedMemory)
            .withDetail("maxMemory", maxMemory)
            .build();
    }
}
