package com.example.demo.service.impl;

import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderVerificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderVerificationServiceImpl implements OrderVerificationService {
    
    private final OrderRepository orderRepository;
    
    public OrderVerificationServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return orderRepository.hasUserPurchasedProduct(userId, productId);
    }
}
