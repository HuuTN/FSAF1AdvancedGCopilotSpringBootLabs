package com.example.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.model.dto.ProductDTO;
import com.example.model.entity.Product;
import com.example.model.entity.Category;
import com.example.service.CategoryService;
import com.example.exception.ResourceNotFoundException;

/**
 * Mapper class for converting between Product entity and ProductDTO
 * Handles the conversion logic and category lookup
 */
@Component
public class ProductMapper {

    @Autowired
    private CategoryService categoryService;

    /**
     * Convert Product entity to ProductDTO
     * 
     * @param product The product entity to convert
     * @return ProductDTO representation
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null,
                product.getAverageRating());
    }

    /**
     * Convert ProductDTO to Product entity
     * Handles category lookup and validation
     * 
     * @param productDTO The ProductDTO to convert
     * @return Product entity
     */
    public Product toEntity(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }

        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        if (productDTO.getAverageRating() != null) {
            product.setAverageRating(productDTO.getAverageRating());
        }

        // Handle category lookup through service
        if (productDTO.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + productDTO.getCategoryId()));
            product.setCategory(category);
        }

        return product;
    }

    /**
     * Update existing Product entity with ProductDTO data
     * Used for update operations where we want to preserve the existing entity
     * 
     * @param existingProduct The existing product entity
     * @param productDTO      The DTO with updated data
     * @return Updated product entity
     */
    public Product updateEntity(Product existingProduct, ProductDTO productDTO) {
        if (existingProduct == null || productDTO == null) {
            return existingProduct;
        }

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setStock(productDTO.getStock());
        if (productDTO.getAverageRating() != null) {
            existingProduct.setAverageRating(productDTO.getAverageRating());
        }

        // Handle category update
        if (productDTO.getCategoryId() != null) {
            Category category = categoryService.getCategoryById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with id: " + productDTO.getCategoryId()));
            existingProduct.setCategory(category);
        }

        return existingProduct;
    }
}
