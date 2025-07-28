package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ProductDTO;
import com.example.usermanagement.dto.ReviewDTO;
import com.example.usermanagement.service.ProductService;
import com.example.usermanagement.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API", description = "CRUD API for Product management")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    @Operation(summary = "Search products with paging, keywords, price")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @RequestParam(required = false) String keywords,
            @RequestParam(required = false) BigDecimal price,
            @RequestParam(required = false) Integer categoryId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(productService.search(keywords, price, categoryId,pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create new product")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return new ResponseEntity<>(productService.create(productDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product by id")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by id")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/count-by-category")
    @Operation(summary = "Count products by category (native query)")
    public ResponseEntity<Integer> countByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(productService.countByCategory(id));
    }

    @PostMapping("/{productId}/reviews")
    public ReviewDTO addReview(@PathVariable Long productId, @RequestParam Long userId, @RequestParam int rating, @RequestParam String comment) {
        return reviewService.addReview(userId, productId, rating, comment);
    }

    @GetMapping("/{productId}/reviews")
    public List<ReviewDTO> getReviews(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }

    @GetMapping("/{productId}/average-rating")
    public Double getAverageRating(@PathVariable Long productId) {
        return reviewService.getAverageRating(productId);
    }
} 