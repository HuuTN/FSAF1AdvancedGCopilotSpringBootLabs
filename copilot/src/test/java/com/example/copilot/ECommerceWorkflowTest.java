package com.example.copilot;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.copilot.dto.CreateOrderRequestDTO;
import com.example.copilot.dto.OrderDTO;
import com.example.copilot.entity.Product;
import com.example.copilot.entity.User;
import com.example.copilot.repository.ProductRepository;
import com.example.copilot.service.OrderService;
import com.example.copilot.service.UserRepository;

@SpringBootTest
@Transactional
class ECommerceWorkflowTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderService orderService;

    private static class TestData {
        User user;
        Product product;
        TestData(User user, Product product) {
            this.user = user;
            this.product = product;
        }
    }

    private TestData setupInitialData() {
        User user = new User();
        user.setName("testuser");
        user.setPassword("password");
        user.setEmail("testuser@example.com");
        user.setRole("USER");
        user = userRepository.save(user);

        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("A product for testing");
        product.setPrice(99.99);
        product.setStockQuantity(100);
        product = productRepository.save(product);

        return new TestData(user, product);
    }

    @Test
    void placeOrder_e2e() {
        TestData data = setupInitialData();
        CreateOrderRequestDTO.CreateOrderItemDTO item = new CreateOrderRequestDTO.CreateOrderItemDTO();
        item.setProductId(data.product.getId());
        item.setQuantity(2);
        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        request.setUserId(data.user.getId());
        request.setItems(Collections.singletonList(item));

        OrderDTO orderDTO = orderService.placeOrder(request);
        assertNotNull(orderDTO);
        assertEquals(data.user.getId(), orderDTO.getUserId());
        assertEquals(1, orderDTO.getOrderItems().size());
        assertEquals(data.product.getId(), orderDTO.getOrderItems().get(0).getProductId());
        assertEquals(2, orderDTO.getOrderItems().get(0).getQuantity());
    }
}
