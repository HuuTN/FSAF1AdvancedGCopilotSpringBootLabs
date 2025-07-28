package com.example.demo.services.service;

import com.example.demo.core.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    Product createProduct(Product product);
    Optional<Product> updateProduct(Long id, Product productDetails);
    boolean deleteProduct(Long id);
}
