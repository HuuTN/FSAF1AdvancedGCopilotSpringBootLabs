package com.example.copilot.service.impl;

import com.example.copilot.core.dto.ProductDTO;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.repository.CategoryRepository;
import com.example.copilot.core.repository.ProductRepository;
import com.example.copilot.exception.ResourceNotFoundException;
import com.example.copilot.service.ProductService;
import com.example.copilot.core.entity.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        return dto;
    }

    private Product toEntity(ProductDTO dto, Product existing) {
        Product product = existing != null ? existing : new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            product.setCategory(category);
        }
        // Không set orderItems để tránh lỗi orphan
        return product;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAll(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        return new PageImpl<>(
            page.getContent().stream().map(this::toDTO).collect(Collectors.toList()),
            pageable,
            page.getTotalElements()
        );
    }

    @Override
    public Optional<ProductDTO> getById(Long id) {
        return productRepository.findById(id).map(this::toDTO);
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        Product product = toEntity(dto, null);
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Override
    public Optional<ProductDTO> update(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing == null) return Optional.empty();
        Product product = toEntity(dto, existing);
        product.setId(id);
        Product saved = productRepository.save(product);
        return Optional.of(toDTO(saved));
    }

    @Override
    public boolean delete(Long id) {
        if (!productRepository.existsById(id)) return false;
        productRepository.deleteById(id);
        return true;
    }

    @Override
    public Page<ProductDTO> search(String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Product> page = productRepository.search(keyword, categoryId, minPrice, maxPrice, pageable);
        return new PageImpl<>(
            page.getContent().stream().map(this::toDTO).collect(Collectors.toList()),
            pageable,
            page.getTotalElements()
        );
    }
}
