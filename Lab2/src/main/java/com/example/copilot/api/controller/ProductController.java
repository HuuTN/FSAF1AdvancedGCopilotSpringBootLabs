package com.example.copilot.api.controller;

import com.example.copilot.core.dto.ProductDTO;
import com.example.copilot.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PostMapping
    public ProductDTO create(@Valid @RequestBody ProductDTO dto) {
        return productService.create(dto);
    }

    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable Long id) {
        return productService.getById(id).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @GetMapping
    public Page<ProductDTO> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable) {
        return productService.search(keyword, categoryId, minPrice, maxPrice, pageable);
    }

    @PutMapping("/{id}")
    public ProductDTO update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        return productService.update(id, dto).orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!productService.delete(id)) throw new RuntimeException("Product not found");
    }
    
    // Optimized performance endpoints using database indexes
    
    @GetMapping("/fast/search-by-name")
    public List<ProductDTO> findByNameFast(@RequestParam String name) {
        return productService.findByNameFast(name);
    }
    
    @GetMapping("/fast/category-price-range")
    public Page<ProductDTO> findByCategoryAndPriceRangeFast(
            @RequestParam Long categoryId,
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            Pageable pageable) {
        return productService.findByCategoryAndPriceRangeFast(categoryId, minPrice, maxPrice, pageable);
    }
    
    @GetMapping("/fast/in-stock")
    public List<ProductDTO> findInStockProducts(@RequestParam(defaultValue = "1") Integer minStock) {
        return productService.findInStockProducts(minStock);
    }
    
    @GetMapping("/fast/top-rated")
    public Page<ProductDTO> findTopRatedProductsFast(
            @RequestParam(defaultValue = "4.0") Double minRating,
            Pageable pageable) {
        return productService.findTopRatedProductsFast(minRating, pageable);
    }
    
    @GetMapping("/fast/category-rating")
    public List<ProductDTO> findByCategoryAndRatingFast(
            @RequestParam Long categoryId,
            @RequestParam Double minRating) {
        return productService.findByCategoryAndRatingFast(categoryId, minRating);
    }
    
    @GetMapping("/fast/price-range-in-stock")
    public Page<ProductDTO> findByPriceRangeInStockFast(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice,
            Pageable pageable) {
        return productService.findByPriceRangeInStockFast(minPrice, maxPrice, pageable);
    }
    
    @GetMapping("/fast/name-category")
    public List<ProductDTO> findByNameAndCategoryFast(
            @RequestParam String name,
            @RequestParam Long categoryId) {
        return productService.findByNameAndCategoryFast(name, categoryId);
    }
}
