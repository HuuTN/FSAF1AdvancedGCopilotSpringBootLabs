
package com.example.demo.apis.controller;

import com.example.demo.cores.dtos.ProductDTO;
import com.example.demo.cores.entity.Product;
import com.example.demo.services.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/products")

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
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @Operation(summary = "Get all products with pagination", description = "Returns a paginated list of all products")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paginated list of products returned successfully")
    })
    @GetMapping("/page")
    public Page<Product> getAllProductsWithPagination(@PageableDefault(size = 10) Pageable pageable) {
        return productService.getAllProducts(pageable);
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
        return productService.createProduct(productDTO);
    }

    @Operation(summary = "Update an existing product", description = "Updates the product with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Optional<Product> updatedProduct = productService.updateProduct(id, productDTO);
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
    // DTO mapping now handled in service layer
}
