package com.example.service;

import com.example.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Product operations including CRUD and advanced search
 * functionality
 */
public interface ProductService {

    // CRUD Operations for Products

    /**
     * Create a new product
     * 
     * @param product The product to create
     * @return The created product
     */
    Product createProduct(Product product);

    /**
     * Get all products with pagination
     * 
     * @param pageable Pagination information
     * @return Page of products
     */
    Page<Product> getAllProducts(Pageable pageable);

    /**
     * Get product by ID
     * 
     * @param id The product ID
     * @return Optional containing the product if found
     */
    Optional<Product> getProductById(Long id);

    /**
     * Update an existing product
     * 
     * @param id             The product ID
     * @param productDetails The updated product details
     * @return The updated product
     */
    Product updateProduct(Long id, Product productDetails);

    /**
     * Delete a product
     * 
     * @param id The product ID
     */
    void deleteProduct(Long id);

    // Advanced Search Logic

    /**
     * Advanced search for products with keyword, category, and price range
     * 
     * @param keyword    Search keyword for product name
     * @param categoryId Category ID to filter by (can be null)
     * @param minPrice   Minimum price filter
     * @param maxPrice   Maximum price filter
     * @param pageable   Pagination information
     * @return Page of matching products
     */
    Page<Product> searchProducts(String keyword, Long categoryId,
            BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable);

    /**
     * Simple search by keyword and max price
     * 
     * @param keyword  Search keyword
     * @param maxPrice Maximum price
     * @param pageable Pagination information
     * @return Page of matching products
     */
    Page<Product> searchProductsByKeyword(String keyword, BigDecimal maxPrice, Pageable pageable);

    // Category Operations

    /**
     * Get count of products by category
     * 
     * @param categoryId The category ID
     * @return Number of products in the category
     */
    int getProductCountByCategory(Long categoryId);

    // Analytics and Reporting

    /**
     * Get top 5 best-selling products
     * 
     * @return List of product data with sales information
     */
    List<Object[]> getTop5BestSellingProducts();

    // Stock Management

    /**
     * Update product stock
     * 
     * @param productId The product ID
     * @param newStock  New stock quantity
     * @return Updated product
     */
    Product updateStock(Long productId, int newStock);

    /**
     * Restore stock for a product (used in order cancellation)
     * 
     * @param productId The product ID
     * @param quantity  Quantity to restore
     */
    void restoreStock(Long productId, int quantity);

    /**
     * Check if product has sufficient stock
     * 
     * @param productId        The product ID
     * @param requiredQuantity Required quantity
     * @return True if sufficient stock available
     */
    boolean hasStock(Long productId, int requiredQuantity);

    /**
     * Reduce stock for a product (used in order processing)
     * 
     * @param productId The product ID
     * @param quantity  Quantity to reduce
     */
    void reduceStock(Long productId, int quantity);
}
