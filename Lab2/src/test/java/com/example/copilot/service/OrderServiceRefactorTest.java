package com.example.copilot.service;

import com.example.copilot.core.entity.User;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.enums.UserRole;
import com.example.copilot.core.repository.UserRepository;
import com.example.copilot.core.repository.ProductRepository;
import com.example.copilot.exception.ResourceNotFoundException;
import com.example.copilot.exception.InsufficientStockException;
import com.example.copilot.service.impl.OrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for refactored OrderServiceImpl private methods
 * Tests the Single Responsibility Principle implementation
 */
public class OrderServiceRefactorTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.USER);
        
        // Setup test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(100.0);
        testProduct.setStockQuantity(10);
    }

    @Test
    @DisplayName("validateAndGetUser should return user when valid ID provided")
    void validateAndGetUser_ValidId_ReturnsUser() throws Exception {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act
        Method method = OrderServiceImpl.class.getDeclaredMethod("validateAndGetUser", Long.class);
        method.setAccessible(true);
        User result = (User) method.invoke(orderService, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("validateAndGetUser should throw exception when user not found")
    void validateAndGetUser_UserNotFound_ThrowsException() throws Exception {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        Method method = OrderServiceImpl.class.getDeclaredMethod("validateAndGetUser", Long.class);
        method.setAccessible(true);
        
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(orderService, 999L);
        });
        
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("validateAndGetUser should throw exception when userId is null")
    void validateAndGetUser_NullUserId_ThrowsException() throws Exception {
        // Act & Assert
        Method method = OrderServiceImpl.class.getDeclaredMethod("validateAndGetUser", Long.class);
        method.setAccessible(true);
        
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(orderService, (Long) null);
        });
        
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("verifyUserExists should pass for valid user")
    void verifyUserExists_ValidUser_PassesValidation() throws Exception {
        // Act & Assert (should not throw exception)
        Method method = OrderServiceImpl.class.getDeclaredMethod("verifyUserExists", User.class);
        method.setAccessible(true);
        
        assertDoesNotThrow(() -> {
            method.invoke(orderService, testUser);
        });
    }

    @Test
    @DisplayName("verifyUserExists should throw exception for null user")
    void verifyUserExists_NullUser_ThrowsException() throws Exception {
        // Act & Assert
        Method method = OrderServiceImpl.class.getDeclaredMethod("verifyUserExists", User.class);
        method.setAccessible(true);
        
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(orderService, (User) null);
        });
        
        assertTrue(exception.getCause() instanceof ResourceNotFoundException);
    }

    @Test
    @DisplayName("validateAndGetProduct should return product when valid ID provided")
    void validateAndGetProduct_ValidId_ReturnsProduct() throws Exception {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        Method method = OrderServiceImpl.class.getDeclaredMethod("validateAndGetProduct", Long.class);
        method.setAccessible(true);
        Product result = (Product) method.invoke(orderService, 1L);
        
        // Assert
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("validateStockAvailability should pass when sufficient stock")
    void validateStockAvailability_SufficientStock_PassesValidation() throws Exception {
        // Act & Assert (should not throw exception)
        Method method = OrderServiceImpl.class.getDeclaredMethod("validateStockAvailability", Product.class, Integer.class);
        method.setAccessible(true);
        
        assertDoesNotThrow(() -> {
            method.invoke(orderService, testProduct, 5); // requesting 5, available 10
        });
    }

    @Test
    @DisplayName("validateStockAvailability should throw exception when insufficient stock")
    void validateStockAvailability_InsufficientStock_ThrowsException() throws Exception {
        // Act & Assert
        Method method = OrderServiceImpl.class.getDeclaredMethod("validateStockAvailability", Product.class, Integer.class);
        method.setAccessible(true);
        
        Exception exception = assertThrows(Exception.class, () -> {
            method.invoke(orderService, testProduct, 15); // requesting 15, available 10
        });
        
        assertTrue(exception.getCause() instanceof InsufficientStockException);
    }

    @Test
    @DisplayName("reserveProductInventory should decrease stock quantity")
    void reserveProductInventory_ValidQuantity_DecreasesStock() throws Exception {
        // Arrange
        int originalStock = testProduct.getStockQuantity();
        int quantityToSubtract = 3;
        
        // Act
        Method method = OrderServiceImpl.class.getDeclaredMethod("reserveProductInventory", Product.class, Integer.class);
        method.setAccessible(true);
        method.invoke(orderService, testProduct, quantityToSubtract);
        
        // Assert
        assertEquals(originalStock - quantityToSubtract, testProduct.getStockQuantity());
        verify(productRepository).save(testProduct);
    }
}
