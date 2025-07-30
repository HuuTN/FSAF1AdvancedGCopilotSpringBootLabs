package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.Category;

public interface CategoryService {
    void addCategory(Category category);

    Category getCategoryById(Long categoryId);

    void updateCategory(Category category);

    void deleteCategory(Long categoryId);

    List<Category> getAllCategories();
}
