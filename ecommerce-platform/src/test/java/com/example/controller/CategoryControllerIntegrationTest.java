package com.example.controller;

import com.example.model.dto.CategoryDTO;
import com.example.model.entity.Category;
import com.example.service.CategoryService;
import com.example.mapper.CategoryMapper;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@DisplayName("Category Controller Integration Tests")
class CategoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryMapper categoryMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Category testCategory;
    private CategoryDTO testCategoryDTO;
    private Category parentCategory;
    private CategoryDTO parentCategoryDTO;
    private Category childCategory;
    private CategoryDTO childCategoryDTO;

    @BeforeEach
    void setUp() {
        // Set up parent category
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Electronics");
        parentCategory.setParent(null);

        parentCategoryDTO = new CategoryDTO();
        parentCategoryDTO.setId(1L);
        parentCategoryDTO.setName("Electronics");
        parentCategoryDTO.setParentId(null);

        // Set up child category
        childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("Laptops");
        childCategory.setParent(parentCategory);

        childCategoryDTO = new CategoryDTO();
        childCategoryDTO.setId(2L);
        childCategoryDTO.setName("Laptops");
        childCategoryDTO.setParentId(1L);

        // Set up test category (same as parent for general tests)
        testCategory = parentCategory;
        testCategoryDTO = parentCategoryDTO;
    }

    @Nested
    @DisplayName("Create Category Tests")
    class CreateCategoryTests {

        @Test
        @DisplayName("Should create category successfully")
        void shouldCreateCategorySuccessfully() throws Exception {
            // Given
            when(categoryMapper.toEntity(any(CategoryDTO.class))).thenReturn(testCategory);
            when(categoryService.createCategory(any(Category.class))).thenReturn(testCategory);
            when(categoryMapper.toDTO(any(Category.class))).thenReturn(testCategoryDTO);

            // When & Then
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(testCategoryDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testCategoryDTO.getId()))
                    .andExpect(jsonPath("$.name").value(testCategoryDTO.getName()))
                    .andExpect(jsonPath("$.parentId").value(testCategoryDTO.getParentId()));

            verify(categoryMapper).toEntity(any(CategoryDTO.class));
            verify(categoryService).createCategory(any(Category.class));
            verify(categoryMapper).toDTO(any(Category.class));
        }

        @Test
        @DisplayName("Should create child category successfully")
        void shouldCreateChildCategorySuccessfully() throws Exception {
            // Given
            when(categoryMapper.toEntity(any(CategoryDTO.class))).thenReturn(childCategory);
            when(categoryService.createCategory(any(Category.class))).thenReturn(childCategory);
            when(categoryMapper.toDTO(any(Category.class))).thenReturn(childCategoryDTO);

            // When & Then
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(childCategoryDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(childCategoryDTO.getId()))
                    .andExpect(jsonPath("$.name").value(childCategoryDTO.getName()))
                    .andExpect(jsonPath("$.parentId").value(childCategoryDTO.getParentId()));

            verify(categoryMapper).toEntity(any(CategoryDTO.class));
            verify(categoryService).createCategory(any(Category.class));
            verify(categoryMapper).toDTO(any(Category.class));
        }

        @Test
        @DisplayName("Should return bad request for invalid category data")
        void shouldReturnBadRequestForInvalidCategoryData() throws Exception {
            // Given
            CategoryDTO invalidDTO = new CategoryDTO();
            invalidDTO.setName(""); // Invalid empty name

            // When & Then
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle missing content type")
        void shouldHandleMissingContentType() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/categories")
                    .content(objectMapper.writeValueAsString(testCategoryDTO)))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

    @Nested
    @DisplayName("Get Categories Tests")
    class GetCategoriesTests {

        @Test
        @DisplayName("Should get all categories with pagination")
        void shouldGetAllCategoriesWithPagination() throws Exception {
            // Given
            List<Category> categories = Arrays.asList(parentCategory, childCategory);
            Page<Category> categoryPage = new PageImpl<>(categories, PageRequest.of(0, 10), 2);

            when(categoryService.getAllCategories(any(PageRequest.class))).thenReturn(categoryPage);
            when(categoryMapper.toDTO(parentCategory)).thenReturn(parentCategoryDTO);
            when(categoryMapper.toDTO(childCategory)).thenReturn(childCategoryDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/categories")
                    .param("page", "0")
                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].id").value(parentCategoryDTO.getId()))
                    .andExpect(jsonPath("$.content[0].name").value(parentCategoryDTO.getName()))
                    .andExpect(jsonPath("$.content[1].id").value(childCategoryDTO.getId()))
                    .andExpect(jsonPath("$.content[1].name").value(childCategoryDTO.getName()))
                    .andExpect(jsonPath("$.totalElements").value(2))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.number").value(0));

            verify(categoryService).getAllCategories(any(PageRequest.class));
        }

        @Test
        @DisplayName("Should get all categories as list")
        void shouldGetAllCategoriesAsList() throws Exception {
            // Given
            List<Category> categories = Arrays.asList(parentCategory, childCategory);

            when(categoryService.getAllCategories()).thenReturn(categories);
            when(categoryMapper.toDTO(parentCategory)).thenReturn(parentCategoryDTO);
            when(categoryMapper.toDTO(childCategory)).thenReturn(childCategoryDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/categories/all"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(parentCategoryDTO.getId()))
                    .andExpect(jsonPath("$[0].name").value(parentCategoryDTO.getName()))
                    .andExpect(jsonPath("$[1].id").value(childCategoryDTO.getId()))
                    .andExpect(jsonPath("$[1].name").value(childCategoryDTO.getName()));

            verify(categoryService).getAllCategories();
        }

        @Test
        @DisplayName("Should get category by ID")
        void shouldGetCategoryById() throws Exception {
            // Given
            when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));
            when(categoryMapper.toDTO(testCategory)).thenReturn(testCategoryDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testCategoryDTO.getId()))
                    .andExpect(jsonPath("$.name").value(testCategoryDTO.getName()))
                    .andExpect(jsonPath("$.parentId").value(testCategoryDTO.getParentId()));

            verify(categoryService).getCategoryById(1L);
            verify(categoryMapper).toDTO(testCategory);
        }

        @Test
        @DisplayName("Should return not found for non-existent category")
        void shouldReturnNotFoundForNonExistentCategory() throws Exception {
            // Given
            when(categoryService.getCategoryById(999L)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(categoryService).getCategoryById(999L);
            verify(categoryMapper, never()).toDTO(any(Category.class));
        }

        @Test
        @DisplayName("Should handle invalid path variable type")
        void shouldHandleInvalidPathVariableType() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/v1/categories/{id}", "invalid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Update Category Tests")
    class UpdateCategoryTests {

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully() throws Exception {
            // Given
            CategoryDTO updatedDTO = new CategoryDTO();
            updatedDTO.setId(1L);
            updatedDTO.setName("Updated Electronics");
            updatedDTO.setParentId(null);

            Category updatedCategory = new Category();
            updatedCategory.setId(1L);
            updatedCategory.setName("Updated Electronics");
            updatedCategory.setParent(null);

            when(categoryMapper.toEntity(any(CategoryDTO.class))).thenReturn(updatedCategory);
            when(categoryService.updateCategory(eq(1L), any(Category.class))).thenReturn(updatedCategory);
            when(categoryMapper.toDTO(updatedCategory)).thenReturn(updatedDTO);

            // When & Then
            mockMvc.perform(put("/api/v1/categories/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedDTO)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(updatedDTO.getId()))
                    .andExpect(jsonPath("$.name").value(updatedDTO.getName()))
                    .andExpect(jsonPath("$.parentId").value(updatedDTO.getParentId()));

            verify(categoryMapper).toEntity(any(CategoryDTO.class));
            verify(categoryService).updateCategory(eq(1L), any(Category.class));
            verify(categoryMapper).toDTO(updatedCategory);
        }

        @Test
        @DisplayName("Should handle missing request body")
        void shouldHandleMissingRequestBody() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/v1/categories/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle malformed JSON")
        void shouldHandleMalformedJSON() throws Exception {
            // When & Then
            mockMvc.perform(put("/api/v1/categories/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{invalid json"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Delete Category Tests")
    class DeleteCategoryTests {

        @Test
        @DisplayName("Should delete category successfully")
        void shouldDeleteCategorySuccessfully() throws Exception {
            // Given
            doNothing().when(categoryService).deleteCategory(1L);

            // When & Then
            mockMvc.perform(delete("/api/v1/categories/{id}", 1L))
                    .andExpect(status().isNoContent());

            verify(categoryService).deleteCategory(1L);
        }

        @Test
        @DisplayName("Should handle deleting non-existent category")
        void shouldHandleDeletingNonExistentCategory() throws Exception {
            // Given
            doThrow(new RuntimeException("Category not found")).when(categoryService).deleteCategory(999L);

            // When & Then
            mockMvc.perform(delete("/api/v1/categories/{id}", 999L))
                    .andExpect(status().isInternalServerError());

            verify(categoryService).deleteCategory(999L);
        }

        @Test
        @DisplayName("Should handle invalid path variable for delete")
        void shouldHandleInvalidPathVariableForDelete() throws Exception {
            // When & Then
            mockMvc.perform(delete("/api/v1/categories/{id}", "invalid"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Hierarchical Operations Tests")
    class HierarchicalOperationsTests {

        @Test
        @DisplayName("Should get root categories")
        void shouldGetRootCategories() throws Exception {
            // Given
            List<Category> rootCategories = Arrays.asList(parentCategory);

            when(categoryService.getRootCategories()).thenReturn(rootCategories);
            when(categoryMapper.toDTO(parentCategory)).thenReturn(parentCategoryDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/categories/root"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(parentCategoryDTO.getId()))
                    .andExpect(jsonPath("$[0].name").value(parentCategoryDTO.getName()))
                    .andExpect(jsonPath("$[0].parentId").doesNotExist());

            verify(categoryService).getRootCategories();
        }

        @Test
        @DisplayName("Should get empty list when no root categories")
        void shouldGetEmptyListWhenNoRootCategories() throws Exception {
            // Given
            when(categoryService.getRootCategories()).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/categories/root"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(categoryService).getRootCategories();
        }

        @Test
        @DisplayName("Should get subcategories")
        void shouldGetSubcategories() throws Exception {
            // Given
            List<Category> subcategories = Arrays.asList(childCategory);

            when(categoryService.getSubcategories(1L)).thenReturn(subcategories);
            when(categoryMapper.toDTO(childCategory)).thenReturn(childCategoryDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{parentId}/subcategories", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(childCategoryDTO.getId()))
                    .andExpect(jsonPath("$[0].name").value(childCategoryDTO.getName()))
                    .andExpect(jsonPath("$[0].parentId").value(childCategoryDTO.getParentId()));

            verify(categoryService).getSubcategories(1L);
        }

        @Test
        @DisplayName("Should get empty list when no subcategories")
        void shouldGetEmptyListWhenNoSubcategories() throws Exception {
            // Given
            when(categoryService.getSubcategories(1L)).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{parentId}/subcategories", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));

            verify(categoryService).getSubcategories(1L);
        }

        @Test
        @DisplayName("Should check if category exists - true")
        void shouldCheckIfCategoryExistsTrue() throws Exception {
            // Given
            when(categoryService.categoryExists(1L)).thenReturn(true);

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{id}/exists", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("true"));

            verify(categoryService).categoryExists(1L);
        }

        @Test
        @DisplayName("Should check if category exists - false")
        void shouldCheckIfCategoryExistsFalse() throws Exception {
            // Given
            when(categoryService.categoryExists(999L)).thenReturn(false);

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{id}/exists", 999L))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string("false"));

            verify(categoryService).categoryExists(999L);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle service exceptions appropriately")
        void shouldHandleServiceExceptionsAppropriately() throws Exception {
            // Given
            when(categoryService.getCategoryById(1L)).thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                    .andExpect(status().isInternalServerError());

            verify(categoryService).getCategoryById(1L);
        }

        @Test
        @DisplayName("Should handle mapper exceptions")
        void shouldHandleMapperExceptions() throws Exception {
            // Given
            when(categoryService.getCategoryById(1L)).thenReturn(Optional.of(testCategory));
            when(categoryMapper.toDTO(testCategory)).thenThrow(new RuntimeException("Mapping error"));

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                    .andExpect(status().isInternalServerError());

            verify(categoryService).getCategoryById(1L);
            verify(categoryMapper).toDTO(testCategory);
        }

        @Test
        @DisplayName("Should handle service layer exceptions")
        void shouldHandleServiceLayerExceptions() throws Exception {
            // Given
            when(categoryService.getCategoryById(1L)).thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                    .andExpect(status().isInternalServerError());

            verify(categoryService).getCategoryById(1L);
        }
    }

    @Nested
    @DisplayName("Edge Cases and Data Validation Tests")
    class EdgeCasesAndDataValidationTests {

        @Test
        @DisplayName("Should handle very large pagination parameters")
        void shouldHandleVeryLargePaginationParameters() throws Exception {
            // Given
            Page<Category> emptyPage = new PageImpl<>(Collections.emptyList(),
                    PageRequest.of(999, 1000), 0);
            when(categoryService.getAllCategories(any(PageRequest.class))).thenReturn(emptyPage);

            // When & Then
            mockMvc.perform(get("/api/v1/categories")
                    .param("page", "999")
                    .param("size", "1000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(0))
                    .andExpect(jsonPath("$.totalElements").value(0))
                    .andExpect(jsonPath("$.number").value(999))
                    .andExpect(jsonPath("$.size").value(1000));

            verify(categoryService).getAllCategories(any(PageRequest.class));
        }

        @Test
        @DisplayName("Should handle null values in request")
        void shouldHandleNullValuesInRequest() throws Exception {
            // Given
            String jsonWithNulls = "{\"id\":null,\"name\":null,\"parentId\":null}";

            // When & Then
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonWithNulls))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should handle special characters in category name")
        void shouldHandleSpecialCharactersInCategoryName() throws Exception {
            // Given
            CategoryDTO specialCharDTO = new CategoryDTO();
            specialCharDTO.setId(1L);
            specialCharDTO.setName("Electronics & Gadgets (New!)");
            specialCharDTO.setParentId(null);

            Category specialCharCategory = new Category();
            specialCharCategory.setId(1L);
            specialCharCategory.setName("Electronics & Gadgets (New!)");

            when(categoryMapper.toEntity(any(CategoryDTO.class))).thenReturn(specialCharCategory);
            when(categoryService.createCategory(any(Category.class))).thenReturn(specialCharCategory);
            when(categoryMapper.toDTO(any(Category.class))).thenReturn(specialCharDTO);

            // When & Then
            mockMvc.perform(post("/api/v1/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(specialCharDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Electronics & Gadgets (New!)"));

            verify(categoryService).createCategory(any(Category.class));
        }
    }
}
