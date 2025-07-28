package com.example.demo.services.service;

import com.example.demo.cores.entity.Category;
import com.example.demo.cores.dtos.CategoryDTO;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(Long id);
    Category createCategory(CategoryDTO categoryDTO);
    Optional<Category> updateCategory(Long id, CategoryDTO categoryDTO);
    boolean deleteCategory(Long id);
}
