package com.example.copilot.service.impl;

import com.example.copilot.core.dto.ProductDTO;
import com.example.copilot.core.entity.Category;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.repository.CategoryRepository;
import com.example.copilot.core.repository.ProductRepository;
import com.example.copilot.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
public class ProductServiceCacheTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CacheManager cacheManager;

    private Long testProductId;

    private Cache.ValueWrapper getCacheValue(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        return cache != null ? cache.get(key) : null;
    }

    @BeforeEach
    void setUp() {
        // Clear all caches before each test
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });

        // Create test data
        Category category = new Category();
        category.setName("Test Category");
        Category savedCategory = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(99.99);
        product.setStockQuantity(10);
        product.setCategory(savedCategory);
        Product savedProduct = productRepository.save(product);
        
        testProductId = savedProduct.getId();
    }

    @Test
    void testProductCachingOnGetById() {
        // First call - should hit database and cache result
        Optional<ProductDTO> firstCall = productService.getById(testProductId);
        assertTrue(firstCall.isPresent());
        assertEquals("Test Product", firstCall.get().getName());

        // Verify cache contains the result
        assertNotNull(getCacheValue("product-details", testProductId));

        // Second call - should return from cache (no database hit)
        Optional<ProductDTO> secondCall = productService.getById(testProductId);
        assertTrue(secondCall.isPresent());
        assertEquals("Test Product", secondCall.get().getName());

        // Both calls should return the same object reference (cached)
        assertEquals(firstCall.get().getName(), secondCall.get().getName());
    }

    @Test
    void testCacheEvictionOnUpdate() {
        // First call to cache the product
        Optional<ProductDTO> cachedProduct = productService.getById(testProductId);
        assertTrue(cachedProduct.isPresent());

        // Verify cache contains the result
        assertNotNull(getCacheValue("product-details", testProductId));

        // Update the product - should evict cache
        ProductDTO updateDto = new ProductDTO();
        updateDto.setId(testProductId);
        updateDto.setName("Updated Product");
        updateDto.setDescription("Updated Description");
        updateDto.setPrice(199.99);
        updateDto.setStockQuantity(20);
        updateDto.setCategoryId(cachedProduct.get().getCategoryId());

        Optional<ProductDTO> updatedProduct = productService.update(testProductId, updateDto);
        assertTrue(updatedProduct.isPresent());

        // Cache should be evicted after update
        assertNull(getCacheValue("product-details", testProductId));

        // Next call should hit database again and cache new result
        Optional<ProductDTO> freshProduct = productService.getById(testProductId);
        assertTrue(freshProduct.isPresent());
        assertEquals("Updated Product", freshProduct.get().getName());
        assertEquals(199.99, freshProduct.get().getPrice());

        // Verify new data is cached
        assertNotNull(getCacheValue("product-details", testProductId));
    }

    @Test
    void testCacheEvictionOnDelete() {
        // First call to cache the product
        Optional<ProductDTO> cachedProduct = productService.getById(testProductId);
        assertTrue(cachedProduct.isPresent());

        // Verify cache contains the result
        assertNotNull(getCacheValue("product-details", testProductId));

        // Delete the product - should evict cache
        boolean deleted = productService.delete(testProductId);
        assertTrue(deleted);

        // Cache should be evicted after delete
        assertNull(getCacheValue("product-details", testProductId));
    }

    @Test
    void testSearchCaching() {
        // First search call - should cache result
        var firstSearchResult = productService.findByNameFast("Test");
        assertFalse(firstSearchResult.isEmpty());

        // Verify search cache contains result
        String searchKey = "name:Test";
        assertNotNull(getCacheValue("product-search", searchKey));

        // Second search call - should return from cache
        var secondSearchResult = productService.findByNameFast("Test");
        assertFalse(secondSearchResult.isEmpty());

        // Results should be equivalent
        assertEquals(firstSearchResult.size(), secondSearchResult.size());
        assertEquals(firstSearchResult.get(0).getName(), secondSearchResult.get(0).getName());
    }

    @Test
    void testCacheManagerConfiguration() {
        // Verify cache manager is properly configured
        assertNotNull(cacheManager);
        
        // Verify expected caches exist
        assertTrue(cacheManager.getCacheNames().contains("product-details"));
        assertTrue(cacheManager.getCacheNames().contains("product-search"));
        assertTrue(cacheManager.getCacheNames().contains("category-details"));
        assertTrue(cacheManager.getCacheNames().contains("user-details"));
    }
}
