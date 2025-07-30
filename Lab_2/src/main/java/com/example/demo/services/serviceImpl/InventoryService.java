package com.example.demo.services.serviceImpl;

import com.example.demo.core.entity.Product;
import com.example.demo.core.repository.ProductRepository;
import com.example.demo.exception.InsufficientStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    @Autowired
    private ProductRepository productRepository;
    
    @Transactional
    public void validateAndUpdateStock(Product product, Integer quantity) {
        validateStockAvailability(product, quantity);
        updateProductStock(product, quantity);
    }
    
    private void validateStockAvailability(Product product, Integer requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
    }
    
    private void updateProductStock(Product product, Integer quantity) {
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
