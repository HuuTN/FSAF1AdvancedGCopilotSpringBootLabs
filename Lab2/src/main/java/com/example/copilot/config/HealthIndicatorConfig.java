package com.example.copilot.config;

import com.example.copilot.monitoring.MaxMemoryHealthIndicator;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Configuration to register custom health indicators with Spring Boot Actuator
 */
@Configuration
public class HealthIndicatorConfig {

    /**
     * Register MaxMemoryHealthIndicator with Spring Boot Actuator
     */
    @Bean("maxMemory")
    public HealthIndicator maxMemoryHealthIndicator(MaxMemoryHealthIndicator memoryIndicator) {
        return new HealthIndicator() {
            @Override
            public Health health() {
                Map<String, Object> healthData = memoryIndicator.health();
                String status = (String) healthData.get("status");
                
                Health.Builder builder = "UP".equals(status) ? Health.up() : Health.down();
                
                // Add all details from our custom indicator
                for (Map.Entry<String, Object> entry : healthData.entrySet()) {
                    if (!"status".equals(entry.getKey())) {
                        builder.withDetail(entry.getKey(), entry.getValue());
                    }
                }
                
                return builder.build();
            }
        };
    }
}
