package com.example.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.exception.ResourceNotFoundException;
import com.example.model.dto.CategoryDTO;
import com.example.model.dto.ProductDTO;
import com.example.model.entity.Category;
import com.example.model.entity.Product;
import com.example.service.CategoryService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Mapper Unit Tests")
class MapperUnitTests {

    @Nested
    @DisplayName("CategoryMapper Tests")
    class CategoryMapperTests {
        @Mock
        private CategoryService categoryService;

        @InjectMocks
        private CategoryMapper categoryMapper;

        private Category category;
        private CategoryDTO categoryDTO;

        @BeforeEach
        void setUp() {
            category = new Category();
            category.setId(1L);
            category.setName("Electronics");

            categoryDTO = new CategoryDTO();
            categoryDTO.setId(1L);
            categoryDTO.setName("Electronics");
        }

        @Test
        @DisplayName("Should convert Category to CategoryDTO")
        void shouldConvertCategoryToCategoryDTO() {
            // When
            CategoryDTO dto = categoryMapper.toDTO(category);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(category.getId());
            assertThat(dto.getName()).isEqualTo(category.getName());
            assertThat(dto.getParentId()).isNull();
        }

        @Test
        @DisplayName("Should convert Category with parent to CategoryDTO")
        void shouldConvertCategoryWithParentToCategoryDTO() {
            // Given
            Category parent = new Category();
            parent.setId(2L);
            parent.setName("Parent Category");
            category.setParent(parent);

            // When
            CategoryDTO dto = categoryMapper.toDTO(category);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(category.getId());
            assertThat(dto.getName()).isEqualTo(category.getName());
            assertThat(dto.getParentId()).isEqualTo(parent.getId());
        }

        @Test
        @DisplayName("Should handle null input in toDTO")
        void shouldHandleNullInputInToDTO() {
            // When
            CategoryDTO dto = categoryMapper.toDTO(null);

            // Then
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("Should convert CategoryDTO with parent to Category")
        void shouldConvertCategoryDTOToCategory() {
            // Given
            categoryDTO.setParentId(null);

            // When
            Category entity = categoryMapper.toEntity(categoryDTO);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(categoryDTO.getId());
            assertThat(entity.getName()).isEqualTo(categoryDTO.getName());
            assertThat(entity.getParent()).isNull();
        }

        @Test
        @DisplayName("Should convert CategoryDTO with parent to Category")
        void shouldConvertCategoryDTOWithParentToCategory() {
            // Given
            Category parent = new Category();
            parent.setId(2L);
            parent.setName("Parent Category");
            categoryDTO.setParentId(2L);

            when(categoryService.getCategoryById(2L))
                    .thenReturn(Optional.of(parent));

            // When
            Category entity = categoryMapper.toEntity(categoryDTO);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(categoryDTO.getId());
            assertThat(entity.getName()).isEqualTo(categoryDTO.getName());
            assertThat(entity.getParent()).isNotNull();
            assertThat(entity.getParent().getId()).isEqualTo(parent.getId());
        }

        @Test
        @DisplayName("Should handle null parent ID in toEntity")
        void shouldHandleNullParentIdInToEntity() {
            // Given
            categoryDTO.setParentId(null);

            // When
            Category entity = categoryMapper.toEntity(categoryDTO);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getParent()).isNull();
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for non-existent parent category in toEntity")
        void shouldHandleNonExistentParentInToEntity() {
            // Given
            categoryDTO.setParentId(999L);
            when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThat(assertThrows(ResourceNotFoundException.class, () -> {
                categoryMapper.toEntity(categoryDTO);
            })).hasMessageContaining("Parent category not found with id: 999");
        }

        @Test
        @DisplayName("Should handle null input in toEntity")
        void shouldHandleNullInputInToEntity() {
            // When
            Category entity = categoryMapper.toEntity(null);

            // Then
            assertThat(entity).isNull();
        }

    }

    @Nested
    @DisplayName("ProductMapper Tests")
    class ProductMapperTests {
        @Mock
        private CategoryService categoryService;

        @InjectMocks
        private ProductMapper productMapper;

        private Product product;
        private ProductDTO productDTO;
        private Category category;

        @BeforeEach
        void setUp() {
            category = new Category();
            category.setId(1L);
            category.setName("Electronics");

            product = new Product();
            product.setId(1L);
            product.setName("Laptop");
            product.setDescription("Gaming Laptop");
            product.setPrice(new BigDecimal("999.99"));
            product.setStock(10);
            product.setCategory(category);

            productDTO = new ProductDTO();
            productDTO.setId(1L);
            productDTO.setName("Laptop");
            productDTO.setDescription("Gaming Laptop");
            productDTO.setPrice(new BigDecimal("999.99"));
            productDTO.setStock(10);
            productDTO.setCategoryId(1L);
            productDTO.setCategoryName("Electronics");
        }

        @Test
        @DisplayName("Should convert Product to ProductDTO")
        void shouldConvertProductToProductDTO() {
            // When
            ProductDTO dto = productMapper.toDTO(product);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(product.getId());
            assertThat(dto.getName()).isEqualTo(product.getName());
            assertThat(dto.getDescription()).isEqualTo(product.getDescription());
            assertThat(dto.getPrice()).isEqualTo(product.getPrice());
            assertThat(dto.getStock()).isEqualTo(product.getStock());
            assertThat(dto.getCategoryId()).isEqualTo(product.getCategory().getId());
            assertThat(dto.getCategoryName()).isEqualTo(product.getCategory().getName());
        }

        @Test
        @DisplayName("Should handle null input in toDTO")
        void shouldHandleNullInputInToDTO() {
            // When
            ProductDTO dto = productMapper.toDTO(null);

            // Then
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("Should convert ProductDTO to Product")
        void shouldConvertProductDTOToProduct() {
            // Given
            when(categoryService.getCategoryById(1L))
                    .thenReturn(Optional.of(category));

            // When
            Product entity = productMapper.toEntity(productDTO);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(productDTO.getId());
            assertThat(entity.getName()).isEqualTo(productDTO.getName());
            assertThat(entity.getDescription()).isEqualTo(productDTO.getDescription());
            assertThat(entity.getPrice()).isEqualTo(productDTO.getPrice());
            assertThat(entity.getStock()).isEqualTo(productDTO.getStock());
            assertThat(entity.getCategory()).isNotNull();
            assertThat(entity.getCategory().getId()).isEqualTo(category.getId());
        }

        @Test
        @DisplayName("Should handle null input in toEntity")
        void shouldHandleNullInputInToEntity() {
            // When
            Product entity = productMapper.toEntity(null);

            // Then
            assertThat(entity).isNull();
        }

        @Test
        @DisplayName("Should handle null category in Product when converting to DTO")
        void shouldHandleNullCategoryInProductToDTO() {
            // Given
            product.setCategory(null);

            // When
            ProductDTO dto = productMapper.toDTO(product);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getCategoryId()).isNull();
            assertThat(dto.getCategoryName()).isNull();
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for non-existent category in ProductDTO to Product")
        void shouldHandleNonExistentCategoryInToEntity() {
            // Given
            productDTO.setCategoryId(999L);
            when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThat(assertThrows(ResourceNotFoundException.class, () -> {
                productMapper.toEntity(productDTO);
            })).hasMessageContaining("Category not found with id: 999");
        }

        @Test
        @DisplayName("Should handle null category ID in ProductDTO")
        void shouldHandleNullCategoryIdInToEntity() {
            // Given
            productDTO.setCategoryId(null);

            // When
            Product entity = productMapper.toEntity(productDTO);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getCategory()).isNull();
        }

        @Test
        @DisplayName("Should handle empty fields in ProductDTO")
        void shouldHandleEmptyFieldsInProductDTO() {
            // Given
            ProductDTO emptyDTO = new ProductDTO();

            // When
            Product entity = productMapper.toEntity(emptyDTO);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull();
            assertThat(entity.getName()).isNull();
            assertThat(entity.getDescription()).isNull();
            assertThat(entity.getPrice()).isNull();
            assertThat(entity.getStock()).isEqualTo(0); // primitive int defaults to 0, not null
            assertThat(entity.getCategory()).isNull();
        }

        @Test
        @DisplayName("Should handle empty fields in Product")
        void shouldHandleEmptyFieldsInProduct() {
            // Given
            Product emptyProduct = new Product();

            // When
            ProductDTO dto = productMapper.toDTO(emptyProduct);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isNull();
            assertThat(dto.getName()).isNull();
            assertThat(dto.getDescription()).isNull();
            assertThat(dto.getPrice()).isNull();
            assertThat(dto.getStock()).isEqualTo(0); // primitive int defaults to 0, not null
            assertThat(dto.getCategoryId()).isNull();
            assertThat(dto.getCategoryName()).isNull();
        }
    }
}
