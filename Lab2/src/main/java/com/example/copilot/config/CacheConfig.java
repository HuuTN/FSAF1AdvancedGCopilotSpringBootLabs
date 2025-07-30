package com.example.copilot.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Cache Configuration Class
 * Enables caching functionality with ConcurrentMapCacheManager for simple in-memory caching
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configures CacheManager with specific cache names
     * Uses ConcurrentMapCacheManager for thread-safe in-memory caching
     * 
     * @return CacheManager instance with predefined cache names
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Define cache names used throughout the application
        cacheManager.setCacheNames(Arrays.asList(
            "product-details",     // Cache for individual product details
            "product-search",      // Cache for product search results
            "category-details",    // Cache for category information
            "user-details"         // Cache for user information
        ));
        
        // Allow creation of additional caches dynamically if needed
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}
