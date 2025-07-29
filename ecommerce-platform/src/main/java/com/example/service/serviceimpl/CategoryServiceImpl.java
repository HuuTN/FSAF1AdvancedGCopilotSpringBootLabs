package com.example.service.serviceimpl;

import com.example.model.entity.Category;
import com.example.repository.CategoryRepository;
import com.example.service.CategoryService;
import com.example.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of CategoryService interface
 * Handles CRUD operations for categories with hierarchical support
 */
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        // Validate parent category if provided
        if (category.getParent() != null && category.getParent().getId() != null) {
            Category parent = categoryRepository.findById(category.getParent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with id: " + category.getParent().getId()));
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Update category fields
        category.setName(categoryDetails.getName());

        // Update parent if provided
        if (categoryDetails.getParent() != null && categoryDetails.getParent().getId() != null) {
            // Prevent circular reference
            if (categoryDetails.getParent().getId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }

            Category parent = categoryRepository.findById(categoryDetails.getParent().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent category not found with id: " + categoryDetails.getParent().getId()));
            category.setParent(parent);
        } else {
            // Clear parent if not provided
            category.setParent(null);
        }

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        // Validate category exists
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }

        // Check if category has children using repository method
        if (categoryRepository.hasChildren(id)) {
            throw new IllegalStateException("Cannot delete category with subcategories. Delete subcategories first.");
        }

        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findRootCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getSubcategories(Long parentId) {
        // Validate parent category exists
        if (!categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Parent category not found with id: " + parentId);
        }

        return categoryRepository.findByParentId(parentId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean categoryExists(Long id) {
        return categoryRepository.existsById(id);
    }
}
