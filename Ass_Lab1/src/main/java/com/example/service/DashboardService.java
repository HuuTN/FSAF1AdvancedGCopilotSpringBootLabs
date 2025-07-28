package com.example.service;

import com.example.repository.OrderRepository;
import com.example.repository.projection.DashboardStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final OrderRepository orderRepository;

    public DashboardService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStats getDashboardStatistics() {
        return orderRepository.getDashboardStats();
    }
}
