package com.example.demo.apis.controller;

import com.example.demo.core.entity.Product;
import com.example.demo.core.dtos.ProductDTO;
import com.example.demo.services.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of products returned successfully")
    })
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Get product by ID", description = "Returns a product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product found and returned"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Product not found");
            error.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new product", description = "Creates a new product with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public Product createProduct(@RequestBody ProductDTO productDTO) {
        // You may want to map ProductDTO to Product here
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        // Category mapping is omitted for simplicity
        return productService.createProduct(product);
    }

    @Operation(summary = "Update an existing product", description = "Updates the product with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        // You may want to map ProductDTO to Product here
        Product productDetails = new Product();
        productDetails.setName(productDTO.getName());
        productDetails.setPrice(productDTO.getPrice());
        productDetails.setStock(productDTO.getStock());
        // Category mapping is omitted for simplicity
        Optional<Product> updatedProduct = productService.updateProduct(id, productDetails);
        if (updatedProduct.isPresent()) {
            return ResponseEntity.ok(updatedProduct.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Product not found");
            error.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a product", description = "Deletes the product with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Product not found");
            error.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
}
