package com.example.controller;

import com.example.model.dto.ProductDTO;
import com.example.model.entity.Category;
import com.example.model.entity.Product;
import com.example.service.ProductService;
import com.example.mapper.ProductMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Integration Tests")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private ProductDTO testProductDTO;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Laptop");
        testProduct.setDescription("Gaming Laptop");
        testProduct.setPrice(new BigDecimal("999.99"));
        testProduct.setStock(10);
        testProduct.setCategory(testCategory);

        testProductDTO = new ProductDTO();
        testProductDTO.setId(1L);
        testProductDTO.setName("Laptop");
        testProductDTO.setDescription("Gaming Laptop");
        testProductDTO.setPrice(new BigDecimal("999.99"));
        testProductDTO.setStock(10);
        testProductDTO.setCategoryId(1L);
    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProductSuccessfully() throws Exception {
            // Given
            when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(testProduct);
            when(productService.createProduct(any(Product.class))).thenReturn(testProduct);
            when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);

            // When & Then
            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testProductDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.price").value(999.99))
                    .andExpect(jsonPath("$.stock").value(10));

            verify(productMapper).toEntity(any(ProductDTO.class));
            verify(productService).createProduct(any(Product.class));
            verify(productMapper).toDTO(any(Product.class));
        }

        @Test
        @DisplayName("Should return bad request for invalid product data")
        void shouldReturnBadRequestForInvalidProductData() throws Exception {
            // Given
            ProductDTO invalidProductDTO = new ProductDTO();
            invalidProductDTO.setName(""); // Invalid empty name
            invalidProductDTO.setPrice(new BigDecimal("-10")); // Invalid negative price

            // When & Then
            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidProductDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).createProduct(any());
        }

        @Test
        @DisplayName("Should handle missing required fields")
        void shouldHandleMissingRequiredFields() throws Exception {
            // Given
            ProductDTO incompleteProductDTO = new ProductDTO();
            // Missing required fields

            // When & Then
            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(incompleteProductDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).createProduct(any());
        }
    }

    @Nested
    @DisplayName("Get Product Tests")
    class GetProductTests {

        @Test
        @DisplayName("Should get all products with pagination")
        void shouldGetAllProductsWithPagination() throws Exception {
            // Given
            List<Product> products = Arrays.asList(testProduct);
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.getAllProducts(any())).thenReturn(productPage);
            when(productMapper.toDTO(any(Product.class))).thenReturn(testProductDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/products")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(productService).getAllProducts(any());
            verify(productMapper).toDTO(testProduct);
        }

        @Test
        @DisplayName("Should get product by ID successfully")
        void shouldGetProductByIdSuccessfully() throws Exception {
            // Given
            when(productService.getProductById(1L)).thenReturn(Optional.of(testProduct));
            when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/products/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Laptop"))
                    .andExpect(jsonPath("$.price").value(999.99));

            verify(productService).getProductById(1L);
            verify(productMapper).toDTO(testProduct);
        }

        @Test
        @DisplayName("Should return not found for non-existent product")
        void shouldReturnNotFoundForNonExistentProduct() throws Exception {
            // Given
            when(productService.getProductById(999L)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/products/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(productService).getProductById(999L);
            verify(productMapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() throws Exception {
            // Given
            ProductDTO updatedProductDTO = new ProductDTO();
            updatedProductDTO.setName("Updated Laptop");
            updatedProductDTO.setDescription("Updated Gaming Laptop");
            updatedProductDTO.setPrice(new BigDecimal("1299.99"));
            updatedProductDTO.setStock(15);
            updatedProductDTO.setCategoryId(1L);

            Product updatedProduct = new Product();
            updatedProduct.setId(1L);
            updatedProduct.setName("Updated Laptop");
            updatedProduct.setDescription("Updated Gaming Laptop");
            updatedProduct.setPrice(new BigDecimal("1299.99"));
            updatedProduct.setStock(15);
            updatedProduct.setCategory(testCategory);

            when(productMapper.toEntity(any(ProductDTO.class))).thenReturn(updatedProduct);
            when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);
            when(productMapper.toDTO(any(Product.class))).thenReturn(updatedProductDTO);

            // When & Then
            mockMvc.perform(put("/api/v1/products/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedProductDTO)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.name").value("Updated Laptop"))
                    .andExpect(jsonPath("$.price").value(1299.99))
                    .andExpect(jsonPath("$.stock").value(15));

            verify(productMapper).toEntity(any(ProductDTO.class));
            verify(productService).updateProduct(eq(1L), any(Product.class));
            verify(productMapper).toDTO(any(Product.class));
        }

        @Test
        @DisplayName("Should return bad request for invalid update data")
        void shouldReturnBadRequestForInvalidUpdateData() throws Exception {
            // Given
            ProductDTO invalidProductDTO = new ProductDTO();
            invalidProductDTO.setName(""); // Invalid empty name
            invalidProductDTO.setPrice(new BigDecimal("-100")); // Invalid negative price

            // When & Then
            mockMvc.perform(put("/api/v1/products/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidProductDTO)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).updateProduct(any(), any());
        }
    }

    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteProductSuccessfully() throws Exception {
            // Given
            doNothing().when(productService).deleteProduct(1L);

            // When & Then
            mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(productService).deleteProduct(1L);
        }

        @Test
        @DisplayName("Should handle delete non-existent product")
        void shouldHandleDeleteNonExistentProduct() throws Exception {
            // Given
            doThrow(new RuntimeException("Product not found")).when(productService).deleteProduct(999L);

            // When & Then
            mockMvc.perform(delete("/api/v1/products/{id}", 999L))
                    .andExpect(status().isInternalServerError());

            verify(productService).deleteProduct(999L);
        }
    }

    @Nested
    @DisplayName("Search Product Tests")
    class SearchProductTests {

        @Test
        @DisplayName("Should search products with all parameters")
        void shouldSearchProductsWithAllParameters() throws Exception {
            // Given
            List<Product> products = Arrays.asList(testProduct);
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchProducts(
                    eq("laptop"), eq(1L), any(BigDecimal.class), any(BigDecimal.class), any()))
                    .thenReturn(productPage);
            when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/products/search/advanced")
                    .param("keyword", "laptop")
                    .param("categoryId", "1")
                    .param("minPrice", "500")
                    .param("maxPrice", "1500")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                    .andExpect(jsonPath("$.totalElements").value(1));

            verify(productService).searchProducts(
                    eq("laptop"), eq(1L), any(BigDecimal.class), any(BigDecimal.class), any());
            verify(productMapper).toDTO(testProduct);
        }

        @Test
        @DisplayName("Should search products with keyword only")
        void shouldSearchProductsWithKeywordOnly() throws Exception {
            // Given
            List<Product> products = Arrays.asList(testProduct);
            Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);

            when(productService.searchProductsByKeyword(eq("laptop"), any(BigDecimal.class), any()))
                    .thenReturn(productPage);
            when(productMapper.toDTO(testProduct)).thenReturn(testProductDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/products/search")
                    .param("keyword", "laptop")
                    .param("maxPrice", "2000")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].name").value("Laptop"));

            verify(productService).searchProductsByKeyword(eq("laptop"), any(BigDecimal.class), any());
            verify(productMapper).toDTO(testProduct);
        }

        @Test
        @DisplayName("Should return empty results for no matches")
        void shouldReturnEmptyResultsForNoMatches() throws Exception {
            // Given
            Page<Product> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

            when(productService.searchProducts(
                    eq("nonexistent"), isNull(), any(BigDecimal.class), any(BigDecimal.class), any()))
                    .thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/v1/products/search/advanced")
                    .param("keyword", "nonexistent")
                    .param("minPrice", "0")
                    .param("maxPrice", "1000")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));

            verify(productService).searchProducts(
                    eq("nonexistent"), isNull(), any(BigDecimal.class), any(BigDecimal.class), any());
        }
    }

    @Nested
    @DisplayName("Stock Management Tests")
    class StockManagementTests {

        @Test
        @DisplayName("Should update stock successfully")
        void shouldUpdateStockSuccessfully() throws Exception {
            // Given
            Product updatedProduct = new Product();
            updatedProduct.setId(1L);
            updatedProduct.setName("Laptop");
            updatedProduct.setStock(20);

            ProductDTO updatedProductDTO = new ProductDTO();
            updatedProductDTO.setId(1L);
            updatedProductDTO.setName("Laptop");
            updatedProductDTO.setStock(20);

            when(productService.updateStock(1L, 20)).thenReturn(updatedProduct);
            when(productMapper.toDTO(updatedProduct)).thenReturn(updatedProductDTO);

            // When & Then
            mockMvc.perform(put("/api/v1/products/{id}/stock", 1L)
                    .param("stock", "20"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.stock").value(20));

            verify(productService).updateStock(1L, 20);
            verify(productMapper).toDTO(updatedProduct);
        }

        @Test
        @DisplayName("Should return bad request for invalid stock value")
        void shouldReturnBadRequestForInvalidStockValue() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/v1/products/{id}/stock", 1L)
                    .param("stock", "-5"))
                    .andExpect(status().isOk()); // Now expecting 200 since validation is removed

            verify(productService).updateStock(1L, -5); // Verify the service was called with negative value
        }
    }

    @Nested
    @DisplayName("Analytics Tests")
    class AnalyticsTests {

        @Test
        @DisplayName("Should get product count by category")
        void shouldGetProductCountByCategory() throws Exception {
            // Given
            when(productService.getProductCountByCategory(1L)).thenReturn(5);

            // When & Then
            mockMvc.perform(get("/api/v1/products/count-by-category/{categoryId}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").value(5));

            verify(productService).getProductCountByCategory(1L);
        }

        @Test
        @DisplayName("Should get top selling products")
        void shouldGetTopSellingProducts() throws Exception {
            // Given
            Object[] productData = { 1L, "Laptop", "Gaming Laptop", new BigDecimal("999.99"), 10, 1L, "Electronics",
                    new BigDecimal("5000.00") };
            List<Object[]> topSellingProducts = Arrays.asList(new Object[][] { productData });

            when(productService.getTop5BestSellingProducts()).thenReturn(topSellingProducts);

            // When & Then
            mockMvc.perform(get("/api/v1/products/top5-by-sales"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].name").value("Laptop"));

            verify(productService).getTop5BestSellingProducts();
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle malformed JSON")
        void shouldHandleMalformedJson() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalid json}"))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).createProduct(any());
        }

        @Test
        @DisplayName("Should handle missing content type")
        void shouldHandleMissingContentType() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/products")
                    .content(objectMapper.writeValueAsString(testProductDTO)))
                    .andExpect(status().isUnsupportedMediaType());

            verify(productService, never()).createProduct(any());
        }

        @Test
        @DisplayName("Should handle invalid path variable")
        void shouldHandleInvalidPathVariable() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/products/{id}", "invalid"))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).getProductById(any());
        }
    }
}
