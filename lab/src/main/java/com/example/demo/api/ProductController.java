package com.example.demo.api;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductReview;
import com.example.demo.service.ProductService;
import com.example.demo.repository.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    // Create
    @PostMapping
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        return toDTO(productService.saveProduct(productDTO));
    }

    // Read all
    @GetMapping("/all")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts().stream().map(this::toDTO).collect(Collectors.toList());
    }



    // Update
    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        productDTO.setId(id);
        return toDTO(productService.saveProduct(productDTO));
    }

    // Delete
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    // Advanced search
    @GetMapping
    public Page<ProductDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.searchProducts(keyword, categoryId, minPrice, maxPrice, pageable)
                .map(this::toDTO);
    }

    // Get all reviews for a product
    @GetMapping("/{id}/reviews")
    public List<ProductReview> getProductReviews(@PathVariable Long id) {
        return productReviewRepository.findAll().stream()
            .filter(r -> r.getProduct() != null && r.getProduct().getId().equals(id))
            .collect(Collectors.toList());
    }

    // Helper methods for mapping
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

    // Xóa hàm toEntity vì đã chuyển logic sang service
}
