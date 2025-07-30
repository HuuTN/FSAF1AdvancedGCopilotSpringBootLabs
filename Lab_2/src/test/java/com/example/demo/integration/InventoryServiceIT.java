package com.example.demo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.demo.core.entity.Category;
import com.example.demo.core.entity.Product;
import com.example.demo.core.entity.User;
import com.example.demo.core.enums.UserRole;
import com.example.demo.core.repository.CategoryRepository;
import com.example.demo.core.repository.ProductRepository;
import com.example.demo.core.repository.UserRepository;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.services.serviceImpl.InventoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
@Transactional
@DisplayName("Inventory Service Integration Tests")
public class InventoryServiceIT {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Electronics");
        testCategory = categoryRepository.save(category);
    }

    /**
     * Test data builder for creating test entities
     */
    private static class TestDataBuilder {
        private String userName = "Test User";
        private String userEmail = "test@example.com";
        private String productName = "Test Product";
        private BigDecimal price = BigDecimal.valueOf(99.99);
        private Integer stock = 10;
        private Category category;

        public TestDataBuilder withUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public TestDataBuilder withUserEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public TestDataBuilder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public TestDataBuilder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public TestDataBuilder withStock(Integer stock) {
            this.stock = stock;
            return this;
        }

        public TestDataBuilder withCategory(Category category) {
            this.category = category;
            return this;
        }

        public TestData build(UserRepository userRepository, ProductRepository productRepository) {
            User user = new User(userName, userEmail);
            user.setRole(UserRole.USER);
            User savedUser = userRepository.save(user);

            Product product = new Product(productName, price, stock, category);
            Product savedProduct = productRepository.save(product);

            return new TestData(savedUser, savedProduct);
        }
    }

    /**
     * Data wrapper class to hold test entities
     */
    private static class TestData {
        final User user;
        final Product product;

        TestData(User user, Product product) {
            this.user = user;
            this.product = product;
        }
    }

    @Nested
    @DisplayName("Stock Validation Tests")
    class StockValidationTests {
        
        @Test
        @DisplayName("Should successfully update stock when sufficient quantity is available")
        void validateAndUpdateStock_WithSufficientStock_ShouldUpdateStock() {
            // Arrange
            TestData testData = new TestDataBuilder()
                .withCategory(testCategory)
                .withStock(10)
                .build(userRepository, productRepository);
            
            int orderQuantity = 5;

            // Act
            inventoryService.validateAndUpdateStock(testData.product, orderQuantity);

            // Assert
            Product updatedProduct = productRepository.findById(testData.product.getId()).orElseThrow();
            assertEquals(5, updatedProduct.getStock());
            assertTrue(updatedProduct.getStock() >= 0, "Stock should not be negative");
        }

        @Test
        @DisplayName("Should throw exception when insufficient stock is available")
        void validateAndUpdateStock_WithInsufficientStock_ShouldThrowException() {
            // Arrange
            TestData testData = new TestDataBuilder()
                .withCategory(testCategory)
                .withStock(10)
                .build(userRepository, productRepository);
            
            int orderQuantity = 15;

            // Act & Assert
            InsufficientStockException exception = assertThrows(
                InsufficientStockException.class,
                () -> inventoryService.validateAndUpdateStock(testData.product, orderQuantity)
            );
            assertEquals("Insufficient stock for product: " + testData.product.getName(), 
                        exception.getMessage());
        }
    }
}
