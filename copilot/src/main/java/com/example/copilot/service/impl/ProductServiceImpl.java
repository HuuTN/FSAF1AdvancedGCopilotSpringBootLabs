package com.example.copilot.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.copilot.dto.CategoryDTO;
import com.example.copilot.dto.ProductDTO;
import com.example.copilot.entity.Category;
import com.example.copilot.entity.Product;
import com.example.copilot.repository.CategoryRepository;
import com.example.copilot.repository.ProductRepository;
import com.example.copilot.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO) {
        Product product = toEntity(productDTO);
        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
            product.setCategory(category);
        }
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Override
    @Cacheable(value = "product-details", key = "#id")
    public ProductDTO getProductById(Long id) {
        Optional<Product> productOpt = productRepository.findById(id);
        return productOpt.map(this::toDTO).orElse(null);
    }

    @Override
    @CacheEvict(value = "product-details", key = "#id")
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setStockQuantity(productDTO.getStockQuantity());
            if (productDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(productDTO.getCategoryId()).orElse(null);
                product.setCategory(category);
            }
            Product updated = productRepository.save(product);
            return toDTO(updated);
        }
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId()).orElse(null);
            category.setParent(parent);
        }
        Category saved = categoryRepository.save(category);
        return toCategoryDTO(saved);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        return categoryOpt.map(this::toCategoryDTO).orElse(null);
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            category.setName(categoryDTO.getName());
            if (categoryDTO.getParentId() != null) {
                Category parent = categoryRepository.findById(categoryDTO.getParentId()).orElse(null);
                category.setParent(parent);
            }
            Category updated = categoryRepository.save(category);
            return toCategoryDTO(updated);
        }
        return null;
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::toCategoryDTO).collect(Collectors.toList());
    }

    // Helper methods for mapping
    private ProductDTO toDTO(Product product) {
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

    private Product toEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }

    private CategoryDTO toCategoryDTO(Category category) {
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryDTO(category.getId(), category.getName(), parentId);
    }
}
