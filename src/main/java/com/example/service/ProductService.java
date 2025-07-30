package com.example.service;

import com.example.dto.ProductDTO;
import com.example.dto.CategoryDTO;
import com.example.entity.Product;
import com.example.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    ProductDTO getProductById(Long id);
    Page<ProductDTO> searchProducts(String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable);

    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
    void deleteCategory(Long id);
    CategoryDTO getCategoryById(Long id);
}
