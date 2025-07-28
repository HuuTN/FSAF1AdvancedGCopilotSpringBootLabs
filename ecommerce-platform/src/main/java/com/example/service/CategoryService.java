package com.example.service;

import com.example.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Category operations including CRUD functionality
 */
public interface CategoryService {

    /**
     * Create a new category
     * 
     * @param category The category to create
     * @return The created category
     */
    Category createCategory(Category category);

    /**
     * Get all categories with pagination
     * 
     * @param pageable Pagination information
     * @return Page of categories
     */
    Page<Category> getAllCategories(Pageable pageable);

    /**
     * Get all categories as a list
     * 
     * @return List of all categories
     */
    List<Category> getAllCategories();

    /**
     * Get category by ID
     * 
     * @param id The category ID
     * @return Optional containing the category if found
     */
    Optional<Category> getCategoryById(Long id);

    /**
     * Update an existing category
     * 
     * @param id              The category ID
     * @param categoryDetails The updated category details
     * @return The updated category
     */
    Category updateCategory(Long id, Category categoryDetails);

    /**
     * Delete a category
     * 
     * @param id The category ID
     */
    void deleteCategory(Long id);

    /**
     * Get all root categories (categories without parent)
     * 
     * @return List of root categories
     */
    List<Category> getRootCategories();

    /**
     * Get all subcategories of a parent category
     * 
     * @param parentId The parent category ID
     * @return List of subcategories
     */
    List<Category> getSubcategories(Long parentId);

    /**
     * Check if category exists
     * 
     * @param id The category ID
     * @return True if category exists
     */
    boolean categoryExists(Long id);
}
