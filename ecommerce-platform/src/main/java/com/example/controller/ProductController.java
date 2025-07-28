package com.example.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.example.model.dto.ProductDTO;
import com.example.model.entity.Product;
import com.example.service.ProductService;
import com.example.mapper.ProductMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

        @Autowired
        private ProductService productService;

        @Autowired
        private ProductMapper productMapper;

        // CRUD Operations

        @Operation(summary = "Create a new product", description = "Create a new product in the system.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Product created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @PostMapping(produces = "application/json")
        public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
                Product product = productMapper.toEntity(productDTO);
                Product createdProduct = productService.createProduct(product);
                ProductDTO dto = productMapper.toDTO(createdProduct);
                return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        }

        @Operation(summary = "Get all products", description = "Retrieve all products with pagination.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
        })
        @GetMapping
        public ResponseEntity<Page<ProductDTO>> getAllProducts(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<Product> products = productService.getAllProducts(PageRequest.of(page, size));
                Page<ProductDTO> dtos = products.map(productMapper::toDTO);
                return ResponseEntity.ok(dtos);
        }

        @Operation(summary = "Get product by ID", description = "Retrieve a product by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product found and returned successfully"),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
                Optional<Product> product = productService.getProductById(id);
                if (product.isPresent()) {
                        ProductDTO dto = productMapper.toDTO(product.get());
                        return ResponseEntity.ok(dto);
                }
                return ResponseEntity.notFound().build();
        }

        @Operation(summary = "Update a product", description = "Update an existing product by ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        @PutMapping(value = "/{id}", produces = "application/json")
        public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id,
                        @Valid @RequestBody ProductDTO productDTO) {
                Product productDetails = productMapper.toEntity(productDTO);
                Product updatedProduct = productService.updateProduct(id, productDetails);
                ProductDTO dto = productMapper.toDTO(updatedProduct);
                return ResponseEntity.ok(dto);
        }

        @Operation(summary = "Delete a product", description = "Delete a product by ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
                productService.deleteProduct(id);
                return ResponseEntity.noContent().build();
        }

        // Advanced Search Operations

        @Operation(summary = "Advanced search products", description = "Search products by keyword, category, and price range with pagination.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Products found and returned successfully")
        })
        @GetMapping("/search/advanced")
        public ResponseEntity<Page<ProductDTO>> searchProducts(
                        @RequestParam(required = false) String keyword,
                        @RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) BigDecimal minPrice,
                        @RequestParam(required = false) BigDecimal maxPrice,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<Product> products = productService.searchProducts(keyword, categoryId, minPrice, maxPrice,
                                PageRequest.of(page, size));
                Page<ProductDTO> dtos = products.map(productMapper::toDTO);
                return ResponseEntity.ok(dtos);
        }

        @Operation(summary = "Top 5 products by total sales amount", description = "Get the top 5 products with the highest total sales amount.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Top 5 products returned successfully")
        })
        @GetMapping("/top5-by-sales")
        public ResponseEntity<List<ProductDTO>> getTop5ProductsByTotalSales() {
                List<Object[]> results = productService.getTop5BestSellingProducts();
                List<ProductDTO> dtos = results.stream().map(obj -> new ProductDTO(
                                ((Number) obj[0]).longValue(), // id
                                (String) obj[1], // name
                                (String) obj[2], // description - not available in the query result
                                (BigDecimal) obj[3], // price
                                ((Number) obj[4]).intValue(), // stock
                                ((Number) obj[5]).longValue(), // categoryId
                                (String) obj[6] // categoryName
                )).toList();
                return ResponseEntity.ok(dtos);
        }

        @Operation(summary = "Search products", description = "Search for products by keyword and max price, with pagination.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Products found and returned successfully")
        })
        @GetMapping("/search")
        public ResponseEntity<Page<ProductDTO>> searchProductsByKeyword(
                        @RequestParam String keyword,
                        @RequestParam BigDecimal maxPrice,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<Product> products = productService.searchProductsByKeyword(keyword, maxPrice,
                                PageRequest.of(page, size));
                Page<ProductDTO> dtos = products.map(productMapper::toDTO);
                return ResponseEntity.ok(dtos);
        }

        @Operation(summary = "Count products by category", description = "Get the number of products in a specific category.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Count returned successfully")
        })
        @GetMapping("/count-by-category/{categoryId}")
        public ResponseEntity<Integer> countByCategory(@PathVariable Long categoryId) {
                int count = productService.getProductCountByCategory(categoryId);
                return ResponseEntity.ok(count);
        }

        // Stock Management Operations

        @Operation(summary = "Update product stock", description = "Update the stock quantity of a product.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Product not found")
        })
        @PutMapping("/{id}/stock")
        public ResponseEntity<ProductDTO> updateStock(@PathVariable Long id, @RequestParam int stock) {
                Product product = productService.updateStock(id, stock);
                ProductDTO dto = productMapper.toDTO(product);
                return ResponseEntity.ok(dto);
        }

        @Operation(summary = "Check product stock", description = "Check if a product has sufficient stock.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Stock check completed")
        })
        @GetMapping("/{id}/stock/check")
        public ResponseEntity<Boolean> checkStock(@PathVariable Long id, @RequestParam int quantity) {
                boolean hasStock = productService.hasStock(id, quantity);
                return ResponseEntity.ok(hasStock);
        }
}
