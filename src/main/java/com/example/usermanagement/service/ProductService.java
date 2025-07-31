package com.example.usermanagement.service;

import com.example.usermanagement.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface ProductService {
    Page<ProductDTO> search(String keywords, BigDecimal price, Integer categoryId,  Pageable pageable);
    ProductDTO getById(Long id);
    ProductDTO create(ProductDTO dto);
    ProductDTO update(Long id, ProductDTO dto);
    void delete(Long id);
    int countByCategory(Long categoryId);
} 