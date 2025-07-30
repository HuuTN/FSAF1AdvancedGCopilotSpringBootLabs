package com.example.demo.apis.controller;

import com.example.demo.core.entity.Product;
import com.example.demo.core.dtos.ProductDTO;
import com.example.demo.services.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of products returned successfully")
    })
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products.stream()
                .map(this::convertToDTO)
                .toList());
    }

    @Operation(summary = "Get product by ID", description = "Returns a product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found and returned"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isPresent()) {
            return ResponseEntity.ok(convertToDTO(productOpt.get()));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Product not found", HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Create a new product", description = "Creates a new product with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        Product product = convertToEntity(productDTO);
        Product created = productService.createProduct(product);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertToDTO(created));
    }

    @Operation(summary = "Update an existing product", description = "Updates the product with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        Product productDetails = convertToEntity(productDTO);
        Optional<Product> updatedOpt = productService.updateProduct(id, productDetails);
        if (updatedOpt.isPresent()) {
            return ResponseEntity.ok(convertToDTO(updatedOpt.get()));
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Product not found", HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Delete a product", description = "Deletes the product with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(createErrorResponse("Product not found", HttpStatus.NOT_FOUND));
    }

    private Map<String, Object> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", message);
        error.put("status", status.value());
        error.put("timestamp", new Date());
        return error;
    }

    private ProductDTO convertToDTO(Product product) {
        return new ProductDTO(
            product.getName(),
            product.getPrice().doubleValue(),
            product.getStock()
        );
    }

    private Product convertToEntity(ProductDTO dto) {
        return new Product(
            dto.getName(),
            BigDecimal.valueOf(dto.getPrice()),
            dto.getStock(),
            null  // Category will be set by service layer
        );
    }
}
