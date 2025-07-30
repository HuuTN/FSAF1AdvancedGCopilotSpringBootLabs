package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import com.example.demo.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    // Product CRUD
    Product saveProduct(ProductDTO productDTO);

    Optional<Product> findProductById(Long id);

    List<Product> getAllProducts();

    Product updateProduct(ProductDTO productDTO);

    void deleteProduct(Long id);

    // Category CRUD
    Category saveCategory(Category category);

    Optional<Category> getCategoryById(Long id);

    List<Category> getAllCategories();

    void deleteCategory(Long id);

    Double getAverageRatingForProduct(Long productId);

    // Advanced search
    Page<Product> searchProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable);
}
