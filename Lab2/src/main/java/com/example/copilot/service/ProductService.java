package com.example.copilot.service;

import com.example.copilot.core.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    // Original methods
    ProductDTO create(ProductDTO dto);
    Optional<ProductDTO> update(Long id, ProductDTO dto);
    boolean delete(Long id);
    Optional<ProductDTO> getById(Long id);
    Page<ProductDTO> getAll(Pageable pageable);
    Page<ProductDTO> search(String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable);
    
    // Optimized performance methods
    List<ProductDTO> findByNameFast(String name);
    Page<ProductDTO> findByCategoryAndPriceRangeFast(Long categoryId, Double minPrice, Double maxPrice, Pageable pageable);
    List<ProductDTO> findInStockProducts(Integer minStock);
    Page<ProductDTO> findTopRatedProductsFast(Double minRating, Pageable pageable);
    List<ProductDTO> findByCategoryAndRatingFast(Long categoryId, Double minRating);
    Page<ProductDTO> findByPriceRangeInStockFast(Double minPrice, Double maxPrice, Pageable pageable);
    List<ProductDTO> findByNameAndCategoryFast(String name, Long categoryId);
}
