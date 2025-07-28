package com.example.service.serviceimpl;

import com.example.model.entity.Product;
import com.example.model.entity.Category;
import com.example.repository.ProductRepository;
import com.example.repository.CategoryRepository;
import com.example.service.ProductService;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.InsufficientStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProductService interface
 * Handles CRUD operations and advanced search functionality for products
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // CRUD Operations for Products

    @Override
    public Product createProduct(Product product) {
        // Validate category exists if provided
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + product.getCategory().getId()));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        // Update product fields
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());

        // Update category if provided
        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            Category category = categoryRepository.findById(productDetails.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + productDetails.getCategory().getId()));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    // Advanced Search Logic

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable) {
        // Set default values if not provided
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = "";
        }
        if (minPrice == null) {
            minPrice = BigDecimal.ZERO;
        }
        if (maxPrice == null) {
            maxPrice = new BigDecimal("999999.99");
        }

        return productRepository.findByKeywordAndCategoryAndPriceRange(
                keyword.trim(), categoryId, minPrice, maxPrice, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProductsByKeyword(String keyword, BigDecimal maxPrice, Pageable pageable) {
        if (maxPrice == null) {
            maxPrice = new BigDecimal("999999.99");
        }
        return productRepository.searchProducts(keyword, maxPrice, pageable);
    }

    // Category Operations

    @Override
    @Transactional(readOnly = true)
    public int getProductCountByCategory(Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }

    // Analytics and Reporting

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTop5BestSellingProducts() {
        return productRepository.findTop5ProductsByTotalAmount();
    }

    // Stock Management

    @Override
    public Product updateStock(Long productId, int newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        product.setStock(newStock);
        return productRepository.save(product);
    }

    @Override
    public void restoreStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasStock(Long productId, int requiredQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        return product.getStock() >= requiredQuantity;
    }

    @Override
    public void reduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product with id: " + productId);
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
