package com.example.controller;

import com.example.entity.Product;
import com.example.repository.ProductRepository;
import com.example.dto.ProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Operation(summary = "Top 5 products by total sales amount", description = "Get the top 5 products with the highest total sales amount.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top 5 products returned successfully")
    })
    @GetMapping("/top5-by-sales")
    public List<ProductDTO> getTop5ProductsByTotalSales() {
        List<Object[]> results = productRepository.findTop5ProductsByTotalAmount();
        return results.stream().map(obj -> new ProductDTO(
                ((Number) obj[0]).longValue(), // id
                (String) obj[1],               // name
                (java.math.BigDecimal) obj[2], // price
                ((Number) obj[3]).intValue(),  // stock
                ((Number) obj[4]).longValue(), // categoryId
                (String) obj[5]                // categoryName
        )).toList();
    }

    @Operation(summary = "Search products", description = "Search for products by keyword and max price, with pagination.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products found and returned successfully")
    })
    @GetMapping("/search")
    public Page<ProductDTO> searchProducts(
            @RequestParam String keyword,
            @RequestParam BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Product> products = productRepository.searchProducts(keyword, maxPrice, PageRequest.of(page, size));
        List<ProductDTO> dtos = products.stream().map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                product.getCategory() != null ? product.getCategory().getName() : null
        )).collect(Collectors.toList());
        return new PageImpl<>(dtos, products.getPageable(), products.getTotalElements());
    }

    @Operation(summary = "Count products by category", description = "Get the number of products in a specific category.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Count returned successfully")
    })
    @GetMapping("/count-by-category/{categoryId}")
    public int countByCategory(@PathVariable Long categoryId) {
        return productRepository.countByCategoryId(categoryId);
    }
}