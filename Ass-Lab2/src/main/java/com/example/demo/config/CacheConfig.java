package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration class for Spring Boot application
 * Enables caching with concurrent map cache manager for better performance
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure ConcurrentMapCacheManager for simple in-memory caching
     * Suitable for single-instance applications or development environments
     * 
     * @return CacheManager configured with concurrent map implementation
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Pre-configure cache names for better performance
        cacheManager.setCacheNames(java.util.Arrays.asList(
            "product-details",
            "product-list",
            "category-products"
        ));
        
        // Allow dynamic cache creation for future cache names
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}
