package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Category;
import com.example.demo.dto.ProductDTO;
import com.example.demo.repository.ProductRepository;

import jakarta.transaction.Transactional;

import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductReviewRepository productReviewRepository;

    // Product CRUD
    @Override
    @Transactional
    public Product saveProduct(ProductDTO productDTO) {
        Product product;
        if (productDTO.getId() != null) {
            product = productRepository.findById(productDTO.getId()).orElse(new Product());
        } else {
            product = new Product();
        }
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        if (productDTO.getCategoryId() != null) {
            categoryRepository.findById(productDTO.getCategoryId())
                .ifPresent(product::setCategory);
        }
        return productRepository.save(product);
    }

    @Override
    @Cacheable(value = "product-details", key = "#id")
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "product-details", key = "#productDTO.id")
    public Product updateProduct(ProductDTO productDTO) {
        Product product = productRepository.findById(productDTO.getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        if (productDTO.getCategoryId() != null) {
            categoryRepository.findById(productDTO.getCategoryId())
                .ifPresent(product::setCategory);
        }
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Category CRUD
    @Override
    @Transactional
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    @Cacheable(value = "categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // Advanced search
    @Override
    public Page<Product> searchProducts(String keyword, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.searchProducts(keyword, categoryId, minPrice, maxPrice, pageable);
    }

    @Override
    public Double getAverageRatingForProduct(Long productId) {
        Double avg = productReviewRepository.findAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }
}
