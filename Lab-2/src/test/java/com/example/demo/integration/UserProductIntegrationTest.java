package com.example.demo.integration;

import com.example.demo.cores.entity.User;
import com.example.demo.cores.entity.Product;
import com.example.demo.cores.entity.Category;
import com.example.demo.cores.enums.UserRole;
import com.example.demo.cores.repository.UserRepository;
import com.example.demo.cores.repository.ProductRepository;
import com.example.demo.cores.repository.CategoryRepository;
import com.example.demo.services.service.ProductService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test example showing refactored data setup
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserProductIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private ProductService productService;

    /**
     * BEFORE REFACTORING: This test method had a very long Arrange block
     */
    @Test
    void testUserCanPurchaseProduct_RefactoredVersion() {
        // Arrange - Now clean and readable
        TestDataWrapper testData = setupInitialData();
        User testUser = testData.getUser();
        Product testProduct = testData.getProduct();
        
        // Act & Assert
        // Verify entities were created successfully
        assertNotNull(testUser, "User should be created");
        assertNotNull(testProduct, "Product should be created");
        assertEquals("Test User", testUser.getName());
        assertEquals("Test Product", testProduct.getName());
        assertNotNull(testProduct.getCategory());
        assertTrue(testProduct.getStock() > 0, "Product should have stock");
    }

    /**
     * Another test using the same setup helper method
     */
    @Test
    void testProductIsAvailableForUser() {
        // Arrange - Reusable setup
        TestDataWrapper testData = setupInitialData();
        Product testProduct = testData.getProduct();
        
        // Act
        Optional<Product> foundProduct = productService.getProductById(testProduct.getId());
        
        // Assert
        assertTrue(foundProduct.isPresent(), "Product should be found");
        assertEquals(testProduct.getName(), foundProduct.get().getName());
        assertTrue(foundProduct.get().getStock() > 0, "Product should have stock available");
    }

    /**
     * Private helper method that extracts the data setup logic
     * Creates and saves a test User and a test Product
     * Returns a wrapper object containing these created entities
     */
    private TestDataWrapper setupInitialData() {
        // Create and save test category
        Category testCategory = new Category();
        testCategory.setName("Electronics");
        testCategory = categoryRepository.save(testCategory);
        
        // Create and save test user
        User testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("testuser@example.com");
        testUser.setUserRole(UserRole.CUSTOMER);
        testUser = userRepository.save(testUser);
        
        // Create and save test product
        Product testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setDescription("A test product for integration testing");
        testProduct.setPrice(99.99);
        testProduct.setStock(10);
        testProduct.setCategory(testCategory);
        testProduct.setAverageRating(4.5);
        testProduct = productRepository.save(testProduct);
        
        return new TestDataWrapper(testUser, testProduct, testCategory);
    }

    /**
     * Wrapper class to hold test data entities
     * Makes it easy to return multiple objects from the setup method
     */
    private static class TestDataWrapper {
        private final User user;
        private final Product product;
        private final Category category;

        public TestDataWrapper(User user, Product product, Category category) {
            this.user = user;
            this.product = product;
            this.category = category;
        }

        public User getUser() {
            return user;
        }

        public Product getProduct() {
            return product;
        }

        public Category getCategory() {
            return category;
        }
    }

    /**
     * Alternative approach: Using an array to return multiple entities
     * This shows another way to return the created entities
     */
    private Object[] setupInitialDataAsArray() {
        TestDataWrapper wrapper = setupInitialData();
        return new Object[]{wrapper.getUser(), wrapper.getProduct(), wrapper.getCategory()};
    }

    /**
     * Example test using the array approach
     */
    @Test
    void testUsingArrayApproach() {
        // Arrange
        Object[] testData = setupInitialDataAsArray();
        User testUser = (User) testData[0];
        Product testProduct = (Product) testData[1];
        Category testCategory = (Category) testData[2];
        
        // Act & Assert
        assertNotNull(testUser);
        assertNotNull(testProduct);
        assertNotNull(testCategory);
        assertEquals(testCategory.getId(), testProduct.getCategory().getId());
    }
}
