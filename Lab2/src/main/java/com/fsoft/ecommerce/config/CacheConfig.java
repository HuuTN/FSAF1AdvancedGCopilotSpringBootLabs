package com.fsoft.ecommerce.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Cache configuration for the e-commerce application.
 * Enables caching with a concurrent map-based cache manager for improved performance.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates a concurrent map-based cache manager.
     * This is suitable for single-instance applications and development environments.
     * For production with multiple instances, consider using Redis or other distributed cache solutions.
     *
     * @return CacheManager instance configured with predefined cache names
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // Pre-configure cache names for better performance and type safety
        cacheManager.setCacheNames(Arrays.asList(
            "products",           // For paginated product lists
            "product",            // For individual product DTOs
            "product-details",    // For individual product entities
            "productsByCategory", // For category-based product searches
            "searchProducts",     // For name-based product searches
            "advancedSearch",     // For multi-criteria product searches
            "fullTextSearch"      // For full-text search results
        ));
        
        // Allow dynamic cache creation for any additional caches needed at runtime
        cacheManager.setAllowNullValues(false);
        
        return cacheManager;
    }
}
