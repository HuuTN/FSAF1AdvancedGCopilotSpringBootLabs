package com.example.demo.service;

import java.util.List;


import com.example.demo.entity.Product;

public interface ProductService {
    void addProduct(Product product);

    Product getProductById(Long productId);

    void updateProduct(Product product);

    void deleteProduct(Long productId);

    List<Product> getAllProducts(String name, String category, int page, int size);
}
