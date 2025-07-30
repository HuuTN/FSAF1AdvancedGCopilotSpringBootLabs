package com.example.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cache configuration for the E-commerce platform
 * Enables caching with a simple concurrent map cache manager
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Configure a simple concurrent map cache manager
     * 
     * @return CacheManager instance
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();

        // Pre-configure cache names for better performance
        cacheManager.setCacheNames(java.util.Arrays.asList(
                "product-details",
                "product-search",
                "category-details"));

        return cacheManager;
    }
}
