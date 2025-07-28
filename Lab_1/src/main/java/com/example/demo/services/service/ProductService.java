package com.example.demo.services.service;

import com.example.demo.cores.entity.Product;
import com.example.demo.cores.dtos.ProductDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    List<Product> getAllProducts();
    Page<Product> getAllProducts(Pageable pageable);
    Optional<Product> getProductById(Long id);
    Product createProduct(ProductDTO productDTO);
    Optional<Product> updateProduct(Long id, ProductDTO productDTO);
    boolean deleteProduct(Long id);
}
