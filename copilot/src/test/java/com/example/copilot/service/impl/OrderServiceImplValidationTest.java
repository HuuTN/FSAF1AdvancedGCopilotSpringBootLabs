package com.example.copilot.service.impl;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.copilot.dto.CreateOrderRequestDTO;
import com.example.copilot.dto.OrderDTO;
import com.example.copilot.entity.Product;
import com.example.copilot.repository.ProductRepository;

@SpringBootTest
class OrderServiceImplValidationTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private ProductRepository productRepository;


    private Product product;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setStockQuantity(10);
        product.setPrice(100.0);
    }

    @Test
    void placeOrder_shouldCreateOrderSuccessfully() {
        CreateOrderRequestDTO request = new CreateOrderRequestDTO();
        CreateOrderRequestDTO.CreateOrderItemDTO item = new CreateOrderRequestDTO.CreateOrderItemDTO();
        item.setProductId(1L);
        item.setQuantity(2);
        request.setItems(Arrays.asList(item));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderDTO result = orderService.placeOrder(request);

        assertNotNull(result);
    }
}
