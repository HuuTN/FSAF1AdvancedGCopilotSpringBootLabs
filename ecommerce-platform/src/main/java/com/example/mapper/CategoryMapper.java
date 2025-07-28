package com.example.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.model.dto.CategoryDTO;
import com.example.model.entity.Category;
import com.example.service.CategoryService;
import com.example.exception.ResourceNotFoundException;

/**
 * Mapper class for converting between Category entity and CategoryDTO
 */
@Component
public class CategoryMapper {

    @Autowired
    private CategoryService categoryService;

    /**
     * Convert Category entity to CategoryDTO
     * 
     * @param category The category entity to convert
     * @return CategoryDTO representation
     */
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getParent() != null ? category.getParent().getId() : null);
    }

    /**
     * Convert CategoryDTO to Category entity
     * Handles parent category lookup and validation
     * 
     * @param categoryDTO The CategoryDTO to convert
     * @return Category entity
     */
    public Category toEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }

        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());

        // Handle parent category lookup
        if (categoryDTO.getParentId() != null) {
            Category parentCategory = categoryService.getCategoryById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with id: " + categoryDTO.getParentId()));
            category.setParent(parentCategory);
        }

        return category;
    }

    /**
     * Update existing Category entity with CategoryDTO data
     * 
     * @param existingCategory The existing category entity
     * @param categoryDTO      The DTO with updated data
     * @return Updated category entity
     */
    public Category updateEntity(Category existingCategory, CategoryDTO categoryDTO) {
        if (existingCategory == null || categoryDTO == null) {
            return existingCategory;
        }

        existingCategory.setName(categoryDTO.getName());

        // Handle parent category update
        if (categoryDTO.getParentId() != null) {
            Category parentCategory = categoryService.getCategoryById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with id: " + categoryDTO.getParentId()));
            existingCategory.setParent(parentCategory);
        } else {
            existingCategory.setParent(null);
        }

        return existingCategory;
    }
}
