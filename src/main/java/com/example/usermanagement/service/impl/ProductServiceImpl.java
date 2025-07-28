package com.example.usermanagement.service.impl;

import com.example.usermanagement.dto.ProductDTO;
import com.example.usermanagement.entity.Category;
import com.example.usermanagement.entity.Product;
import com.example.usermanagement.exception.EntityNotFoundException;
import com.example.usermanagement.mapper.ProductMapper;
import com.example.usermanagement.repository.CategoryRepository;
import com.example.usermanagement.repository.ProductRepository;
import com.example.usermanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Page<ProductDTO> search(String keywords, BigDecimal price, Integer categoryId, Pageable pageable) {
        return productRepository.search(keywords, price, categoryId, pageable).map(productMapper::toDTO);
    }

    @Override
    public ProductDTO getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId()));
        Product product = productMapper.toEntity(dto, category);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + dto.getCategoryId()));
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(category);
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public int countByCategory(Long categoryId) {
        return productRepository.countByCategory(categoryId);
    }
} 