package com.example.copilot.service;

import com.example.copilot.core.dto.CreateOrderRequestDTO;
import com.example.copilot.core.entity.Category;
import com.example.copilot.core.entity.Order;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.entity.User;
import com.example.copilot.core.enums.UserRole;
import com.example.copilot.service.impl.OrderServiceImpl;
import com.example.copilot.core.repository.CategoryRepository;
import com.example.copilot.core.repository.OrderRepository;
import com.example.copilot.core.repository.ProductRepository;
import com.example.copilot.core.repository.UserRepository;
import com.example.copilot.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceUnitTest {
    @Autowired private OrderServiceImpl orderService;
    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private CategoryRepository categoryRepository;

    private Long userId;
    private Long productId;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("123");
        user.setRole(UserRole.USER);
        userId = userRepository.save(user).getId();
        Category category = new Category();
        category.setName("Test Category");
        categoryRepository.save(category);
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(10.0);
        product.setStockQuantity(0); // hết hàng
        product.setCategory(category);
        productId = productRepository.save(product).getId();
    }

    @Test
    void testPlaceOrder_whenStockIsInsufficient_thenThrowsException() {
        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setUserId(userId);
        CreateOrderRequestDTO.OrderItemRequest item = new CreateOrderRequestDTO.OrderItemRequest();
        item.setProductId(productId);
        item.setQuantity(1);
        request.setItems(java.util.Collections.singletonList(item));
        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(request));
    }
}
