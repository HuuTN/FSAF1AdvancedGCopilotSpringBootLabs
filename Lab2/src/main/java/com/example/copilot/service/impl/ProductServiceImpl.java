package com.example.copilot.service.impl;

import com.example.copilot.core.dto.ProductDTO;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.repository.CategoryRepository;
import com.example.copilot.core.repository.ProductRepository;
import com.example.copilot.exception.ResourceNotFoundException;
import com.example.copilot.service.ProductService;
import com.example.copilot.core.entity.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    @Cacheable(value = "product-details", key = "#id")
    public Optional<ProductDTO> getById(Long id) {
        // FIXED: Proper Optional handling instead of returning null
        return productRepository.findById(id).map(this::toDTO);
    }

    /**
     * Alternative method that directly returns Product entity - fixed version
     * Cached version for direct entity access
     */
    @Cacheable(value = "product-details", key = "#id")
    public Product findProductById(Long id) {
        // FIXED: Throw exception instead of returning null
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        Product product = toEntity(dto, null);
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    @Override
    @CacheEvict(value = "product-details", key = "#id")
    public Optional<ProductDTO> update(Long id, ProductDTO dto) {
        Product existing = productRepository.findById(id).orElse(null);
        if (existing == null) return Optional.empty();
        Product product = toEntity(dto, existing);
        product.setId(id);
        Product saved = productRepository.save(product);
        return Optional.of(toDTO(saved));
    }

    @Override
    @CacheEvict(value = "product-details", key = "#id")
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
    
    // Optimized performance methods using database indexes with caching
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "product-search", key = "'name:' + #name")
    public List<ProductDTO> findByNameFast(String name) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "product-search", key = "'category-price:' + #categoryId + ':' + #minPrice + ':' + #maxPrice + ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public Page<ProductDTO> findByCategoryAndPriceRangeFast(Long categoryId, Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Product> page = productRepository.findByCategoryAndPriceRange(categoryId, minPrice, maxPrice, pageable);
        return new PageImpl<>(
            page.getContent().stream().map(this::toDTO).collect(Collectors.toList()),
            pageable,
            page.getTotalElements()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findInStockProducts(Integer minStock) {
        List<Product> products = productRepository.findByStockQuantityGreaterThanEqual(minStock);
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findTopRatedProductsFast(Double minRating, Pageable pageable) {
        Page<Product> page = productRepository.findTopRatedProducts(minRating, pageable);
        return new PageImpl<>(
            page.getContent().stream().map(this::toDTO).collect(Collectors.toList()),
            pageable,
            page.getTotalElements()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findByCategoryAndRatingFast(Long categoryId, Double minRating) {
        List<Product> products = productRepository.findByCategoryAndMinRating(categoryId, minRating);
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findByPriceRangeInStockFast(Double minPrice, Double maxPrice, Pageable pageable) {
        Page<Product> page = productRepository.findByPriceRangeInStock(minPrice, maxPrice, pageable);
        return new PageImpl<>(
            page.getContent().stream().map(this::toDTO).collect(Collectors.toList()),
            pageable,
            page.getTotalElements()
        );
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> findByNameAndCategoryFast(String name, Long categoryId) {
        List<Product> products = productRepository.findByNameAndCategory(name, categoryId);
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
