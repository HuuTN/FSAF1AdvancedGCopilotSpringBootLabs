package com.example.copilot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.copilot.dto.ProductDTO;
import com.example.copilot.entity.Product;
import com.example.copilot.repository.ProductRepository;

@Service
public class ProductSearchService {
    @Autowired
    private ProductRepository productRepository;

    public Page<ProductDTO> searchProducts(String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.searchProducts(keyword, categoryId, minPrice, maxPrice, pageable)
                .map(product -> toProductDTO((Product) product));
    }

    private ProductDTO toProductDTO(Product product) {
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : null;
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            categoryId
        );
    }
}
