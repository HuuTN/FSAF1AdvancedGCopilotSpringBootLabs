package com.example.copilot.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the application.
 * Enables caching with a simple concurrent map cache manager for development/testing.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configures a simple in-memory cache manager using ConcurrentHashMap.
     * For production environments, consider using Redis, Hazelcast, or Caffeine.
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Pre-define cache names for better performance and explicit configuration
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "product-details",
            "product-search",
            "category-details"
        ));
        
        // Allow dynamic cache creation if needed
        cacheManager.setAllowNullValues(true);
        
        return cacheManager;
    }
}
