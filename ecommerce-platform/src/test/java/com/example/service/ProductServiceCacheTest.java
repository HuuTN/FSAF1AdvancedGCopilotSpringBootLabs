package com.example.service;

import com.example.config.CacheConfig;
import com.example.model.entity.Category;
import com.example.model.entity.Product;
import com.example.repository.CategoryRepository;
import com.example.repository.ProductRepository;
import com.example.service.serviceimpl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Cache tests for ProductService
 * Verifies that caching is working correctly for product operations
 */
@SpringBootTest(classes = { ProductServiceImpl.class, CacheConfig.class })
@TestPropertySource(properties = {
        "spring.cache.type=simple"
})
@DisplayName("Product Service Cache Tests")
class ProductServiceCacheTest {

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Clear all caches before each test
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });

        // Reset mocks
        reset(productRepository);
        reset(categoryRepository);

        // Setup test data
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("Gaming Laptop");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setStock(10);
        testProduct.setCategory(testCategory);
    }

    @Test
    @DisplayName("Should cache product on first retrieval")
    void shouldCacheProductOnFirstRetrieval() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When - First call
        Optional<Product> result1 = productService.getProductById(1L);

        // When - Second call
        Optional<Product> result2 = productService.getProductById(1L);

        // Then
        assertThat(result1).isPresent();
        assertThat(result2).isPresent();
        assertThat(result1.get().getName()).isEqualTo("Laptop");
        assertThat(result2.get().getName()).isEqualTo("Laptop");

        // Repository should be called only once due to caching
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should evict cache when product is updated")
    void shouldEvictCacheWhenProductIsUpdated() {
        // Given - Create fresh product objects for this test
        Product freshProduct = new Product();
        freshProduct.setId(1L);
        freshProduct.setName("Laptop");
        freshProduct.setDescription("Gaming Laptop");
        freshProduct.setPrice(new BigDecimal("999.99"));
        freshProduct.setStock(10);
        freshProduct.setCategory(testCategory);

        when(productRepository.findById(1L)).thenReturn(Optional.of(freshProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(freshProduct);

        // When - Cache the product first
        productService.getProductById(1L);

        // When - Update the product (should evict cache)
        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Laptop");
        updatedProduct.setCategory(testCategory);
        productService.updateProduct(1L, updatedProduct);

        // When - Retrieve again (should hit repository again)
        productService.getProductById(1L);

        // Then - Repository should be called three times (initial cache, updateProduct
        // fetch, after eviction)
        verify(productRepository, times(3)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should evict cache when stock is updated")
    void shouldEvictCacheWhenStockIsUpdated() {
        // Given - Create fresh product objects for this test
        Product freshProduct = new Product();
        freshProduct.setId(1L);
        freshProduct.setName("Laptop");
        freshProduct.setDescription("Gaming Laptop");
        freshProduct.setPrice(new BigDecimal("999.99"));
        freshProduct.setStock(10);
        freshProduct.setCategory(testCategory);

        when(productRepository.findById(1L)).thenReturn(Optional.of(freshProduct));
        when(productRepository.save(any(Product.class))).thenReturn(freshProduct);

        // When - Cache the product first
        Optional<Product> result1 = productService.getProductById(1L);

        // When - Update stock (should evict cache)
        productService.updateStock(1L, 20);

        // When - Retrieve again (should hit repository again)
        Optional<Product> result2 = productService.getProductById(1L);

        // Then - Verify that both calls returned the expected product
        assertThat(result1).isPresent();
        assertThat(result2).isPresent();
        assertThat(result1.get().getName()).isEqualTo("Laptop");
        assertThat(result2.get().getName()).isEqualTo("Laptop");

        // Repository should be called at least twice (once for update stock, once after
        // cache eviction)
        verify(productRepository, atLeast(2)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }
}
