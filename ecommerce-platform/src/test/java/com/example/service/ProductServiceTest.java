package com.example.service;

import com.example.exception.InsufficientStockException;
import com.example.exception.ResourceNotFoundException;
import com.example.model.entity.Category;
import com.example.model.entity.Product;
import com.example.repository.CategoryRepository;
import com.example.repository.ProductRepository;
import com.example.service.serviceimpl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private Category testCategory;
    private Pageable pageable;

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

        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperations {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProductSuccessfully() {
            // Given
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            // When
            Product result = productService.createProduct(testProduct);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Laptop");
            assertThat(result.getCategory()).isEqualTo(testCategory);
            verify(categoryRepository).findById(1L);
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should throw exception when category not found during creation")
        void shouldThrowExceptionWhenCategoryNotFoundDuringCreation() {
            // Given
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.createProduct(testProduct))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Category not found with id: 1");

            verify(categoryRepository).findById(1L);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should get all products with pagination")
        void shouldGetAllProductsWithPagination() {
            // Given
            List<Product> products = Arrays.asList(testProduct);
            Page<Product> productPage = new PageImpl<>(products, pageable, 1);
            when(productRepository.findAll(pageable)).thenReturn(productPage);

            // When
            Page<Product> result = productService.getAllProducts(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Laptop");
            verify(productRepository).findAll(pageable);
        }

        @Test
        @DisplayName("Should get product by ID")
        void shouldGetProductById() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

            // When
            Optional<Product> result = productService.getProductById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Laptop");
            verify(productRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when product not found by ID")
        void shouldReturnEmptyWhenProductNotFoundById() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            // When
            Optional<Product> result = productService.getProductById(1L);

            // Then
            assertThat(result).isEmpty();
            verify(productRepository).findById(1L);
        }

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProductSuccessfully() {
            // Given
            Product updatedDetails = new Product();
            updatedDetails.setName("Updated Laptop");
            updatedDetails.setDescription("Updated Gaming Laptop");
            updatedDetails.setPrice(new BigDecimal("1299.99"));
            updatedDetails.setStock(15);
            updatedDetails.setCategory(testCategory);

            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            // When
            Product result = productService.updateProduct(1L, updatedDetails);

            // Then
            assertThat(result).isNotNull();
            verify(productRepository).findById(1L);
            verify(categoryRepository).findById(1L);
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent product")
        void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.updateProduct(1L, testProduct))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 1");

            verify(productRepository).findById(1L);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteProductSuccessfully() {
            // Given
            when(productRepository.existsById(1L)).thenReturn(true);

            // When
            productService.deleteProduct(1L);

            // Then
            verify(productRepository).existsById(1L);
            verify(productRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent product")
        void shouldThrowExceptionWhenDeletingNonExistentProduct() {
            // Given
            when(productRepository.existsById(1L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> productService.deleteProduct(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 1");

            verify(productRepository).existsById(1L);
            verify(productRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Search Operations")
    class SearchOperations {

        @Test
        @DisplayName("Should search products with all parameters")
        void shouldSearchProductsWithAllParameters() {
            // Given
            List<Product> products = Arrays.asList(testProduct);
            Page<Product> productPage = new PageImpl<>(products, pageable, 1);
            when(productRepository.findByKeywordAndCategoryAndPriceRange(
                    eq("laptop"), eq(1L), any(BigDecimal.class), any(BigDecimal.class), eq(pageable)))
                    .thenReturn(productPage);

            // When
            Page<Product> result = productService.searchProducts(
                    "laptop", 1L, new BigDecimal("500"), new BigDecimal("1500"), pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            verify(productRepository).findByKeywordAndCategoryAndPriceRange(
                    eq("laptop"), eq(1L), any(BigDecimal.class), any(BigDecimal.class), eq(pageable));
        }

        @Test
        @DisplayName("Should search products by keyword and max price")
        void shouldSearchProductsByKeywordAndMaxPrice() {
            // Given
            List<Product> products = Arrays.asList(testProduct);
            Page<Product> productPage = new PageImpl<>(products, pageable, 1);
            when(productRepository.searchProducts(eq("laptop"), any(BigDecimal.class), eq(pageable)))
                    .thenReturn(productPage);

            // When
            Page<Product> result = productService.searchProductsByKeyword("laptop", new BigDecimal("1500"), pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            verify(productRepository).searchProducts(eq("laptop"), any(BigDecimal.class), eq(pageable));
        }

        @Test
        @DisplayName("Should handle empty keyword in search")
        void shouldHandleEmptyKeywordInSearch() {
            // Given
            List<Product> products = Arrays.asList(testProduct);
            Page<Product> productPage = new PageImpl<>(products, pageable, 1);
            when(productRepository.findByKeywordAndCategoryAndPriceRange(
                    eq(""), isNull(), any(BigDecimal.class), any(BigDecimal.class), eq(pageable)))
                    .thenReturn(productPage);

            // When
            Page<Product> result = productService.searchProducts("", null, BigDecimal.ZERO, new BigDecimal("9999"),
                    pageable);

            // Then
            assertThat(result).isNotNull();
            verify(productRepository).findByKeywordAndCategoryAndPriceRange(
                    eq(""), isNull(), any(BigDecimal.class), any(BigDecimal.class), eq(pageable));
        }
    }

    @Nested
    @DisplayName("Stock Management")
    class StockManagement {

        @Test
        @DisplayName("Should check stock availability - sufficient stock")
        void shouldCheckStockAvailabilitySufficientStock() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

            // When
            boolean result = productService.hasStock(1L, 5);

            // Then
            assertThat(result).isTrue();
            verify(productRepository).findById(1L);
        }

        @Test
        @DisplayName("Should check stock availability - insufficient stock")
        void shouldCheckStockAvailabilityInsufficientStock() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

            // When
            boolean result = productService.hasStock(1L, 15);

            // Then
            assertThat(result).isFalse();
            verify(productRepository).findById(1L);
        }

        @Test
        @DisplayName("Should throw exception when checking stock for non-existent product")
        void shouldThrowExceptionWhenCheckingStockForNonExistentProduct() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> productService.hasStock(1L, 5))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 1");

            verify(productRepository).findById(1L);
        }

        @Test
        @DisplayName("Should update stock successfully")
        void shouldUpdateStockSuccessfully() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            // When
            Product result = productService.updateStock(1L, 20);

            // Then
            assertThat(result).isNotNull();
            verify(productRepository).findById(1L);
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should reduce stock successfully")
        void shouldReduceStockSuccessfully() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            // When
            productService.reduceStock(1L, 3);

            // Then
            verify(productRepository).findById(1L);
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should throw exception when reducing stock with insufficient quantity")
        void shouldThrowExceptionWhenReducingStockWithInsufficientQuantity() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

            // When & Then
            assertThatThrownBy(() -> productService.reduceStock(1L, 15))
                    .isInstanceOf(InsufficientStockException.class)
                    .hasMessageContaining("Insufficient stock for product with id: 1");

            verify(productRepository).findById(1L);
            verify(productRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should restore stock successfully")
        void shouldRestoreStockSuccessfully() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            // When
            productService.restoreStock(1L, 5);

            // Then
            verify(productRepository).findById(1L);
            verify(productRepository).save(testProduct);
        }
    }

    @Nested
    @DisplayName("Analytics and Reporting")
    class AnalyticsAndReporting {

        @Test
        @DisplayName("Should get product count by category")
        void shouldGetProductCountByCategory() {
            // Given
            when(productRepository.countByCategoryId(1L)).thenReturn(5);

            // When
            int result = productService.getProductCountByCategory(1L);

            // Then
            assertThat(result).isEqualTo(5);
            verify(productRepository).countByCategoryId(1L);
        }

        @Test
        @DisplayName("Should get top 5 best selling products")
        void shouldGetTop5BestSellingProducts() {
            // Given
            Object[] productData = { 1L, "Laptop", "Gaming Laptop", new BigDecimal("999.99"), 10, 1L, "Electronics",
                    new BigDecimal("5000.00") };
            List<Object[]> bestSellingProducts = Arrays.asList(new Object[][] { productData });
            when(productRepository.findTop5ProductsByTotalAmount()).thenReturn(bestSellingProducts);

            // When
            List<Object[]> result = productService.getTop5BestSellingProducts();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            verify(productRepository).findTop5ProductsByTotalAmount();
        }
    }

    @Nested
    @DisplayName("Edge Cases and Validation")
    class EdgeCasesAndValidation {

        @Test
        @DisplayName("Should handle null category during product creation")
        void shouldHandleNullCategoryDuringProductCreation() {
            // Given
            testProduct.setCategory(null);
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            // When
            Product result = productService.createProduct(testProduct);

            // Then
            assertThat(result).isNotNull();
            verify(categoryRepository, never()).findById(any());
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should handle category with null ID during product creation")
        void shouldHandleCategoryWithNullIdDuringProductCreation() {
            // Given
            testCategory.setId(null);
            testProduct.setCategory(testCategory);
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            // When
            Product result = productService.createProduct(testProduct);

            // Then
            assertThat(result).isNotNull();
            verify(categoryRepository, never()).findById(any());
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("Should handle zero stock during stock check")
        void shouldHandleZeroStockDuringStockCheck() {
            // Given
            testProduct.setStock(0);
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

            // When
            boolean result = productService.hasStock(1L, 1);

            // Then
            assertThat(result).isFalse();
            verify(productRepository).findById(1L);
        }

        @Test
        @DisplayName("Should handle negative quantity in reduce stock")
        void shouldHandleNegativeQuantityInReduceStock() {
            // Given
            when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

            // When & Then
            assertThatThrownBy(() -> productService.reduceStock(1L, -5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantity cannot be negative");

            verify(productRepository).findById(1L);
            verify(productRepository, never()).save(any());
        }
    }
}
