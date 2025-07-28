package com.example.service.impl;

import com.example.dto.ProductDTO;
import com.example.dto.CategoryDTO;
import com.example.entity.Product;
import com.example.entity.Category;
import com.example.repository.ProductRepository;
import com.example.repository.CategoryRepository;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        if (productDTO.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(productDTO.getCategoryId());
            category.ifPresent(product::setCategory);
        }
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Optional<Product> optional = productRepository.findById(id);
        if (optional.isEmpty()) return null;
        Product product = optional.get();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        if (productDTO.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(productDTO.getCategoryId());
            category.ifPresent(product::setCategory);
        }
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id).map(this::toDTO).orElse(null);
    }

    @Override
    public Page<ProductDTO> searchProducts(String keyword, Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.advancedSearch(keyword, categoryId, minPrice, maxPrice, pageable)
                .map(this::toDTO);
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // Implement as needed
        return null;
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        // Implement as needed
        return null;
    }

    @Override
    public void deleteCategory(Long id) {
        // Implement as needed
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        // Implement as needed
        return null;
    }

    private ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
    }
}
