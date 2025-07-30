package com.example.copilot.service.impl;

import com.example.copilot.core.dto.DashboardStatsDTO;
import com.example.copilot.core.repository.OrderRepository;
import com.example.copilot.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Dashboard Service Implementation
 * Following Optimized Prompt: single database call for efficiency
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        // Using single efficient native SQL query instead of multiple calls
        List<Object[]> results = orderRepository.getDashboardStats();
        
        // Extract first (and only) row results with safe type casting
        Object[] result = results.get(0);
        
        // Handle different numeric types from database
        BigDecimal totalRevenue = result[0] instanceof BigDecimal ? 
            (BigDecimal) result[0] : 
            new BigDecimal(result[0].toString());
            
        Long totalOrders = ((Number) result[1]).longValue();
        Long newCustomersThisMonth = ((Number) result[2]).longValue();
        
        return new DashboardStatsDTO(totalRevenue, totalOrders, newCustomersThisMonth);
    }
}
