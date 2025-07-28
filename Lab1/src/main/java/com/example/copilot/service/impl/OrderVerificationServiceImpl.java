package com.example.copilot.service.impl;

import com.example.copilot.core.enums.OrderStatus;
import com.example.copilot.core.repository.OrderRepository;
import com.example.copilot.service.OrderVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of OrderVerificationService
 * Handles order verification logic separated from ReviewService
 * Following Single Responsibility Principle
 */
@Service
public class OrderVerificationServiceImpl implements OrderVerificationService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        return hasUserPurchasedProductWithStatus(userId, productId, OrderStatus.DELIVERED);
    }

    @Override
    public boolean hasUserPurchasedProductWithStatus(Long userId, Long productId, OrderStatus status) {
        return orderRepository.existsByUserIdAndOrderItemsProductIdAndStatus(userId, productId, status);
    }
}
