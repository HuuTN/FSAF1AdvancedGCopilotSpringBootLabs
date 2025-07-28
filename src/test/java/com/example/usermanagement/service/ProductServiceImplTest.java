package com.example.usermanagement.service;

import com.example.usermanagement.dto.ProductDTO;
import com.example.usermanagement.entity.Category;
import com.example.usermanagement.entity.Product;
import com.example.usermanagement.exception.EntityNotFoundException;
import com.example.usermanagement.mapper.ProductMapper;
import com.example.usermanagement.repository.CategoryRepository;
import com.example.usermanagement.repository.ProductRepository;
import com.example.usermanagement.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        category = Category.builder().id(1L).name("Cat").build();
        product = Product.builder().id(2L).name("Prod").price(BigDecimal.TEN).stock(5).category(category).build();
        productDTO = ProductDTO.builder().id(2L).name("Prod").price(BigDecimal.TEN).stock(5).categoryId(1L).build();
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.getById(2L));
    }

    @Test
    void getById_ReturnsProductDTO() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);
        ProductDTO result = productService.getById(2L);
        assertNotNull(result);
        assertEquals("Prod", result.getName());
    }

    @Test
    void create_CategoryNotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.create(productDTO));
    }

    @Test
    void create_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(productDTO, category)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);
        ProductDTO result = productService.create(productDTO);
        assertNotNull(result);
        assertEquals("Prod", result.getName());
    }

    @Test
    void update_ProductNotFound_ThrowsException() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.update(2L, productDTO));
    }

    @Test
    void update_CategoryNotFound_ThrowsException() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.update(2L, productDTO));
    }

    @Test
    void update_Success() {
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);
        ProductDTO result = productService.update(2L, productDTO);
        assertNotNull(result);
        assertEquals("Prod", result.getName());
    }

    @Test
    void delete_ProductNotFound_ThrowsException() {
        when(productRepository.existsById(2L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> productService.delete(2L));
    }

    @Test
    void delete_Success() {
        when(productRepository.existsById(2L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(2L);
        assertDoesNotThrow(() -> productService.delete(2L));
    }

    @Test
    void countByCategory_ReturnsValue() {
        when(productRepository.countByCategory(1L)).thenReturn(3);
        assertEquals(3, productService.countByCategory(1L));
    }

    @Test
    void search_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.search(any(), any(), any(), any())).thenReturn(new PageImpl<>(List.of(product)));
        when(productMapper.toDTO(any(Product.class))).thenReturn(productDTO);
        Page<ProductDTO> page = productService.search("key", BigDecimal.TEN,null, pageable);
        assertEquals(1, page.getTotalElements());
        assertEquals("Prod", page.getContent().get(0).getName());
    }
} 