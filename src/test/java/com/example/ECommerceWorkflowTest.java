package com.example;

import com.example.dto.CreateOrderRequestDTO;
import com.example.dto.OrderDTO;
import com.example.entity.Category;
import com.example.entity.Product;
import com.example.entity.User;
import com.example.repository.CategoryRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ECommerceWorkflowTest {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testCompleteOrderWorkflow_AfterRefactoring() {
        // Arrange
        TestData testData = setupInitialData();

        CreateOrderRequestDTO.OrderItemRequest laptopItem = new CreateOrderRequestDTO.OrderItemRequest();
        laptopItem.setProductId(testData.laptop.getId());
        laptopItem.setQuantity(1);

        CreateOrderRequestDTO.OrderItemRequest mouseItem = new CreateOrderRequestDTO.OrderItemRequest();
        mouseItem.setProductId(testData.mouse.getId());
        mouseItem.setQuantity(2);

        CreateOrderRequestDTO orderRequest = new CreateOrderRequestDTO();
        orderRequest.setUserId(testData.user.getId());
        orderRequest.setShippingAddress("456 Oak Ave, Another City, State");
        orderRequest.setItems(Arrays.asList(laptopItem, mouseItem));

        // Act
        OrderDTO result = orderService.placeOrder(orderRequest);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(testData.user.getId(), result.getUserId());
        assertEquals("PENDING", result.getStatus());
        assertEquals("456 Oak Ave, Another City, State", result.getShippingAddress());
        assertEquals(2, result.getItems().size());
        
        BigDecimal expectedTotal = testData.laptop.getPrice().add(testData.mouse.getPrice().multiply(BigDecimal.valueOf(2)));
        assertEquals(expectedTotal, result.getTotalAmount());
    }

    /**
     * Helper method to create and save test data entities.
     * @return TestData containing the created User and Products
     */
    private TestData setupInitialData() {
        // Create and save category
        Category electronics = new Category();
        electronics.setName("Electronics");
        electronics.setDescription("Electronic devices and gadgets");
        electronics = categoryRepository.save(electronics);

        // Create and save test user
        User testUser = new User();
        testUser.setUsername("johndoe");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("password123");
        testUser.setAddress("123 Main St, City, State");
        testUser = userRepository.save(testUser);

        // Create and save test products
        Product laptop = createProduct("Gaming Laptop", "High-performance gaming laptop with RTX graphics", 
                                     new BigDecimal("1299.99"), 10, electronics);
        Product mouse = createProduct("Wireless Mouse", "Ergonomic wireless mouse with precision tracking", 
                                    new BigDecimal("29.99"), 50, electronics);

        return new TestData(testUser, laptop, mouse);
    }

    /**
     * Helper method to create a product with the given parameters.
     */
    private Product createProduct(String name, String description, BigDecimal price, int stock, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setActive(true);
        product.setCategory(category);
        return productRepository.save(product);
    }

    /**
     * Data class to hold test entities created by setupInitialData method.
     */
    private static class TestData {
        final User user;
        final Product laptop;
        final Product mouse;

        TestData(User user, Product laptop, Product mouse) {
            this.user = user;
            this.laptop = laptop;
            this.mouse = mouse;
        }
    }
}