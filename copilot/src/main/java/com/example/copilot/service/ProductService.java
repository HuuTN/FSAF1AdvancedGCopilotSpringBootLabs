package com.example.copilot.service;

import java.util.List;

import com.example.copilot.dto.CategoryDTO;
import com.example.copilot.dto.ProductDTO;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO);
    ProductDTO getProductById(Long id);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    List<ProductDTO> getAllProducts();

    CategoryDTO addCategory(CategoryDTO categoryDTO);
    CategoryDTO getCategoryById(Long id);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    List<CategoryDTO> getAllCategories();
}
