package com.example.usermanagement.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public ConcurrentMapCacheManager cacheManager() {
        return new ConcurrentMapCacheManager("product-details");
    }

    @Bean
    public HealthIndicator cacheHealthIndicator() {
        return () -> {
            if (cacheManager().getCache("product-details") != null) {
                return Health.up().withDetail("cache", "Available").build();
            } else {
                return Health.down().withDetail("cache", "Not Available").build();
            }
        };
    }
}