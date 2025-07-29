package com.example.service;

import com.example.exception.ResourceNotFoundException;
import com.example.model.entity.Category;
import com.example.repository.CategoryRepository;
import com.example.service.serviceimpl.CategoryServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category parentCategory;
    private Category childCategory;
    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        // Setup parent category
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Electronics");

        // Setup child category
        childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("Laptops");
        childCategory.setParent(parentCategory);

        // Setup category list
        categoryList = Arrays.asList(parentCategory, childCategory);
    }

    @Test
    void createCategory_WithValidCategory_ShouldReturnCreatedCategory() {
        // Given
        Category newCategory = new Category();
        newCategory.setName("Smartphones");
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // When
        Category result = categoryService.createCategory(newCategory);

        // Then
        assertNotNull(result);
        assertEquals("Smartphones", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_WithParent_ShouldSetParentAndReturnCategory() {
        // Given
        Category newCategory = new Category();
        newCategory.setName("Smartphones");
        newCategory.setParent(parentCategory);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // When
        Category result = categoryService.createCategory(newCategory);

        // Then
        assertNotNull(result);
        assertEquals("Smartphones", result.getName());
        assertNotNull(result.getParent());
        assertEquals("Electronics", result.getParent().getName());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void getAllCategories_WithPagination_ShouldReturnPageOfCategories() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> categoryPage = new PageImpl<>(categoryList, pageable, categoryList.size());
        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        // When
        Page<Category> result = categoryService.getAllCategories(pageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("Electronics", result.getContent().get(0).getName());
        assertEquals("Laptops", result.getContent().get(1).getName());

        verify(categoryRepository, times(1)).findAll(pageable);
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        // Given
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // When
        List<Category> result = categoryService.getAllCategories();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("Laptops", result.get(1).getName());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_WithValidId_ShouldReturnCategory() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        // When
        Optional<Category> result = categoryService.getCategoryById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Electronics", result.get().getName());

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void updateCategory_WithValidData_ShouldReturnUpdatedCategory() {
        // Given
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // When
        Category result = categoryService.updateCategory(1L, updatedCategory);

        // Then
        assertNotNull(result);
        assertEquals("Updated Electronics", result.getName());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_WithInvalidId_ShouldThrowException() {
        // Given
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Electronics");

        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(999L, updatedCategory));

        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WithCircularReference_ShouldThrowException() {
        // Given
        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("Electronics");
        updatedCategory.setParent(parentCategory); // Setting parent as itself

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoryService.updateCategory(1L, updatedCategory));

        assertEquals("Category cannot be its own parent", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WithValidId_ShouldDeleteCategory() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.hasChildren(1L)).thenReturn(false);
        doNothing().when(categoryRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));

        // Then
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).hasChildren(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCategory_WithInvalidId_ShouldThrowException() {
        // Given
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(999L));

        assertEquals("Category not found with id: 999", exception.getMessage());
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteCategory_WithChildCategories_ShouldThrowException() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.hasChildren(1L)).thenReturn(true);

        // When & Then
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> categoryService.deleteCategory(1L));

        assertEquals("Cannot delete category with subcategories. Delete subcategories first.", exception.getMessage());
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void getRootCategories_ShouldReturnListOfRootCategories() {
        // Given
        when(categoryRepository.findRootCategories()).thenReturn(List.of(parentCategory));

        // When
        List<Category> result = categoryService.getRootCategories();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());

        verify(categoryRepository, times(1)).findRootCategories();
    }

    @Test
    void getSubcategories_WithValidParentId_ShouldReturnListOfSubcategories() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(true);
        when(categoryRepository.findByParentId(1L)).thenReturn(List.of(childCategory));

        // When
        List<Category> result = categoryService.getSubcategories(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptops", result.get(0).getName());

        verify(categoryRepository, times(1)).findByParentId(1L);
    }

    @Test
    void getSubcategories_WithInvalidParentId_ShouldThrowException() {
        // Given
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getSubcategories(999L));

        assertEquals("Parent category not found with id: 999", exception.getMessage());
        verify(categoryRepository, never()).findByParentId(anyLong());
    }

    @Test
    void categoryExists_WithExistingId_ShouldReturnTrue() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = categoryService.categoryExists(1L);

        // Then
        assertTrue(result);
        verify(categoryRepository, times(1)).existsById(1L);
    }

    @Test
    void categoryExists_WithNonExistingId_ShouldReturnFalse() {
        // Given
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // When
        boolean result = categoryService.categoryExists(999L);

        // Then
        assertFalse(result);
        verify(categoryRepository, times(1)).existsById(999L);
    }
}
