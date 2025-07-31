package com.example.demo.service;

import com.example.demo.cores.entity.User;
import com.example.demo.cores.entity.Product;
import com.example.demo.cores.entity.Category;
import com.example.demo.cores.dtos.UserDTO;
import com.example.demo.cores.enums.UserRole;
import com.example.demo.cores.repository.UserRepository;
import com.example.demo.services.serviceImpl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * BEFORE REFACTORING: This test had a very long Arrange block
     * Now it's clean and readable using the setupInitialData helper method
     */
    @Test
    void createUser_shouldReturnSavedUserDto() {
        // Arrange - Clean and concise
        TestDataWrapper testData = setupInitialData();
        User mockUser = testData.getUser();
        
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        
        UserDTO inputDto = new UserDTO();
        inputDto.setName(mockUser.getName());
        inputDto.setEmail(mockUser.getEmail());
        inputDto.setUserRole(UserRole.CUSTOMER);
        
        // Act
        UserDTO savedUserDto = userService.createUser(inputDto);
        
        // Assert
        assertNotNull(savedUserDto);
        assertEquals(mockUser.getName(), savedUserDto.getName());
        assertEquals(mockUser.getEmail(), savedUserDto.getEmail());
    }

    /**
     * Another test using the same setup helper - demonstrates reusability
     */
    @Test
    void getUserById_shouldReturnUserDto() {
        // Arrange - Reusing the same setup
        TestDataWrapper testData = setupInitialData();
        User mockUser = testData.getUser();
        
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(mockUser));
        
        // Act
        UserDTO foundUserDto = userService.getUserById(1L);
        
        // Assert
        assertNotNull(foundUserDto);
        assertEquals("John Doe", foundUserDto.getName());
        assertEquals("john.doe@example.com", foundUserDto.getEmail());
    }

    /**
     * Private helper method that extracts the data setup logic
     * Creates mock User and Product entities for testing
     * Returns a wrapper object containing these created entities
     */
    private TestDataWrapper setupInitialData() {
        // Create mock category
        Category mockCategory = new Category();
        mockCategory.setId(1L);
        mockCategory.setName("Electronics");
        
        // Create mock user with all necessary test data
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("John Doe");
        mockUser.setEmail("john.doe@example.com");
        
        // Create mock product with all necessary test data
        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test Laptop");
        mockProduct.setPrice(999.99);
        mockProduct.setStock(5);
        mockProduct.setCategory(mockCategory);
        mockProduct.setAverageRating(4.5);
        
        return new TestDataWrapper(mockUser, mockProduct, mockCategory);
    }

    /**
     * Wrapper class to hold test data entities
     * Makes it easy to return multiple objects from the setup method
     * This approach is more type-safe than using arrays
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
}
