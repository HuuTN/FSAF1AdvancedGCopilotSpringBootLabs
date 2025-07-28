package com.example.copilot.service;

import com.example.copilot.core.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductService {
    ProductDTO create(ProductDTO dto);
    Optional<ProductDTO> update(Long id, ProductDTO dto);
    boolean delete(Long id);
    Optional<ProductDTO> getById(Long id);
    Page<ProductDTO> getAll(Pageable pageable);
    Page<ProductDTO> search(String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable);
}
