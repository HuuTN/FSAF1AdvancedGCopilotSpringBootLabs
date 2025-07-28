package com.example.repository;

import com.example.model.entity.Category;
import com.example.model.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Product Repository Tests")
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(com.example.config.JpaConfig.class)
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Category testCategory;
    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;

    @BeforeEach
    void setUp() {
        // Create test category
        testCategory = new Category();
        testCategory.setName("Electronics");
        entityManager.persistAndFlush(testCategory);

        // Create test products
        testProduct1 = new Product();
        testProduct1.setName("Gaming Laptop");
        testProduct1.setDescription("High performance gaming laptop");
        testProduct1.setPrice(new BigDecimal("1299.99"));
        testProduct1.setStock(10);
        testProduct1.setCategory(testCategory);
        entityManager.persistAndFlush(testProduct1);

        testProduct2 = new Product();
        testProduct2.setName("Office Laptop");
        testProduct2.setDescription("Business laptop for office work");
        testProduct2.setPrice(new BigDecimal("799.99"));
        testProduct2.setStock(15);
        testProduct2.setCategory(testCategory);
        entityManager.persistAndFlush(testProduct2);

        testProduct3 = new Product();
        testProduct3.setName("Smartphone");
        testProduct3.setDescription("Latest smartphone model");
        testProduct3.setPrice(new BigDecimal("599.99"));
        testProduct3.setStock(20);
        testProduct3.setCategory(testCategory);
        entityManager.persistAndFlush(testProduct3);
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save product successfully")
        void shouldSaveProductSuccessfully() {
            // Given
            Product newProduct = new Product();
            newProduct.setName("Tablet");
            newProduct.setDescription("Android tablet");
            newProduct.setPrice(new BigDecimal("299.99"));
            newProduct.setStock(30);
            newProduct.setCategory(testCategory);

            // When
            Product savedProduct = productRepository.save(newProduct);

            // Then
            assertThat(savedProduct).isNotNull();
            assertThat(savedProduct.getId()).isNotNull();
            assertThat(savedProduct.getName()).isEqualTo("Tablet");
            assertThat(savedProduct.getCategory()).isEqualTo(testCategory);
        }

        @Test
        @DisplayName("Should find product by ID")
        void shouldFindProductById() {
            // When
            Optional<Product> foundProduct = productRepository.findById(testProduct1.getId());

            // Then
            assertThat(foundProduct).isPresent();
            assertThat(foundProduct.get().getName()).isEqualTo("Gaming Laptop");
            assertThat(foundProduct.get().getPrice()).isEqualTo(new BigDecimal("1299.99"));
        }

        @Test
        @DisplayName("Should return empty when product not found by ID")
        void shouldReturnEmptyWhenProductNotFoundById() {
            // When
            Optional<Product> foundProduct = productRepository.findById(999L);

            // Then
            assertThat(foundProduct).isEmpty();
        }

        @Test
        @DisplayName("Should find all products")
        void shouldFindAllProducts() {
            // When
            List<Product> products = productRepository.findAll();

            // Then
            assertThat(products).hasSize(3);
            assertThat(products).extracting(Product::getName)
                    .containsExactlyInAnyOrder("Gaming Laptop", "Office Laptop", "Smartphone");
        }

        @Test
        @DisplayName("Should delete product by ID")
        void shouldDeleteProductById() {
            // Given
            Long productId = testProduct1.getId();

            // When
            productRepository.deleteById(productId);
            entityManager.flush();

            // Then
            Optional<Product> deletedProduct = productRepository.findById(productId);
            assertThat(deletedProduct).isEmpty();

            List<Product> remainingProducts = productRepository.findAll();
            assertThat(remainingProducts).hasSize(2);
        }

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() {
            // Given
            Product productToUpdate = testProduct1;
            productToUpdate.setName("Updated Gaming Laptop");
            productToUpdate.setPrice(new BigDecimal("1399.99"));

            // When
            Product updatedProduct = productRepository.save(productToUpdate);
            entityManager.flush();

            // Then
            assertThat(updatedProduct.getName()).isEqualTo("Updated Gaming Laptop");
            assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("1399.99"));

            // Verify in database
            Optional<Product> foundProduct = productRepository.findById(testProduct1.getId());
            assertThat(foundProduct).isPresent();
            assertThat(foundProduct.get().getName()).isEqualTo("Updated Gaming Laptop");
        }
    }

    @Nested
    @DisplayName("Search Operations")
    class SearchOperations {

        @Test
        @DisplayName("Should search products by keyword and max price")
        void shouldSearchProductsByKeywordAndMaxPrice() {
            // Given
            String keyword = "laptop";
            BigDecimal maxPrice = new BigDecimal("1000.00");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Office Laptop");
            assertThat(result.getContent().get(0).getPrice()).isLessThanOrEqualTo(maxPrice);
        }

        @Test
        @DisplayName("Should search products with case insensitive keyword")
        void shouldSearchProductsWithCaseInsensitiveKeyword() {
            // Given
            String keyword = "LAPTOP";
            BigDecimal maxPrice = new BigDecimal("2000.00");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Product::getName)
                    .containsExactlyInAnyOrder("Gaming Laptop", "Office Laptop");
        }

        @Test
        @DisplayName("Should find products by keyword, category and price range")
        void shouldFindProductsByKeywordCategoryAndPriceRange() {
            // Given
            String keyword = "laptop";
            Long categoryId = testCategory.getId();
            BigDecimal minPrice = new BigDecimal("500.00");
            BigDecimal maxPrice = new BigDecimal("1500.00");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.findByKeywordAndCategoryAndPriceRange(
                    keyword, categoryId, minPrice, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Product::getName)
                    .containsExactlyInAnyOrder("Gaming Laptop", "Office Laptop");
        }

        @Test
        @DisplayName("Should find products with null category filter")
        void shouldFindProductsWithNullCategoryFilter() {
            // Given
            String keyword = "laptop";
            Long categoryId = null; // No category filter
            BigDecimal minPrice = new BigDecimal("0.00");
            BigDecimal maxPrice = new BigDecimal("2000.00");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.findByKeywordAndCategoryAndPriceRange(
                    keyword, categoryId, minPrice, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Product::getName)
                    .containsExactlyInAnyOrder("Gaming Laptop", "Office Laptop");
        }

        @Test
        @DisplayName("Should return empty result for non-matching keyword")
        void shouldReturnEmptyResultForNonMatchingKeyword() {
            // Given
            String keyword = "nonexistent";
            BigDecimal maxPrice = new BigDecimal("1000.00");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should return empty result when max price is too low")
        void shouldReturnEmptyResultWhenMaxPriceIsTooLow() {
            // Given
            String keyword = "laptop";
            BigDecimal maxPrice = new BigDecimal("100.00");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Category Operations")
    class CategoryOperations {

        @Test
        @DisplayName("Should count products by category")
        void shouldCountProductsByCategory() {
            // When
            int count = productRepository.countByCategoryId(testCategory.getId());

            // Then
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("Should return zero count for non-existent category")
        void shouldReturnZeroCountForNonExistentCategory() {
            // When
            int count = productRepository.countByCategoryId(999L);

            // Then
            assertThat(count).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Pagination Tests")
    class PaginationTests {

        @Test
        @DisplayName("Should paginate search results correctly")
        void shouldPaginateSearchResultsCorrectly() {
            // Given
            String keyword = "laptop";
            BigDecimal maxPrice = new BigDecimal("2000.00");
            Pageable pageable = PageRequest.of(0, 1); // First page, 1 item per page

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getTotalPages()).isEqualTo(2);
            assertThat(result.hasNext()).isTrue();
            assertThat(result.hasPrevious()).isFalse();
        }

        @Test
        @DisplayName("Should handle second page correctly")
        void shouldHandleSecondPageCorrectly() {
            // Given
            String keyword = "laptop";
            BigDecimal maxPrice = new BigDecimal("2000.00");
            Pageable pageable = PageRequest.of(1, 1); // Second page, 1 item per page

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.hasNext()).isFalse();
            assertThat(result.hasPrevious()).isTrue();
        }

        @Test
        @DisplayName("Should handle empty page")
        void shouldHandleEmptyPage() {
            // Given
            String keyword = "laptop";
            BigDecimal maxPrice = new BigDecimal("2000.00");
            Pageable pageable = PageRequest.of(5, 10); // Page beyond available data

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Validation")
    class EdgeCasesAndValidation {

        @Test
        @DisplayName("Should handle empty keyword search")
        void shouldHandleEmptyKeywordSearch() {
            // Given
            String keyword = "";
            BigDecimal maxPrice = new BigDecimal("1000.00");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            // Should return products where price <= maxPrice
            assertThat(result.getContent()).hasSize(2); // Office Laptop and Smartphone
        }

        @Test
        @DisplayName("Should handle very large max price")
        void shouldHandleVeryLargeMaxPrice() {
            // Given
            String keyword = "laptop";
            BigDecimal maxPrice = new BigDecimal("999999.99");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("Should handle zero max price")
        void shouldHandleZeroMaxPrice() {
            // Given
            String keyword = "laptop";
            BigDecimal maxPrice = BigDecimal.ZERO;
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("Should handle partial keyword match")
        void shouldHandlePartialKeywordMatch() {
            // Given
            String keyword = "lap"; // Partial match for "laptop"
            BigDecimal maxPrice = new BigDecimal("2000.00");
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.searchProducts(keyword, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Product::getName)
                    .containsExactlyInAnyOrder("Gaming Laptop", "Office Laptop");
        }

        @Test
        @DisplayName("Should handle price range boundaries")
        void shouldHandlePriceRangeBoundaries() {
            // Given
            String keyword = "";
            Long categoryId = testCategory.getId();
            BigDecimal minPrice = new BigDecimal("799.99"); // Exact price of Office Laptop
            BigDecimal maxPrice = new BigDecimal("799.99"); // Exact price of Office Laptop
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<Product> result = productRepository.findByKeywordAndCategoryAndPriceRange(
                    keyword, categoryId, minPrice, maxPrice, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Office Laptop");
        }
    }

    @Nested
    @DisplayName("Entity Relationships")
    class EntityRelationships {

        @Test
        @DisplayName("Should maintain category relationship")
        void shouldMaintainCategoryRelationship() {
            // When
            Optional<Product> product = productRepository.findById(testProduct1.getId());

            // Then
            assertThat(product).isPresent();
            assertThat(product.get().getCategory()).isNotNull();
            assertThat(product.get().getCategory().getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("Should load category eagerly when needed")
        void shouldLoadCategoryEagerlyWhenNeeded() {
            // When
            Product product = productRepository.findById(testProduct1.getId()).orElseThrow();

            // Clear the persistence context to test lazy loading
            entityManager.clear();

            // Access category should still work due to the relationship
            Category category = product.getCategory();

            // Then
            assertThat(category).isNotNull();
            assertThat(category.getName()).isEqualTo("Electronics");
        }
    }
}
