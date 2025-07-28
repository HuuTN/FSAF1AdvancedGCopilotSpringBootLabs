package com.example.copilot.service.impl;

import com.example.copilot.core.dto.DashboardStatsDTO;
import com.example.copilot.core.repository.OrderRepository;
import com.example.copilot.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
        try {
            // Using single efficient native SQL query instead of multiple calls
            Object[] result = orderRepository.getDashboardStats();
            
            // Extract results with safe type casting and null handling
            BigDecimal totalRevenue = result[0] != null ? (BigDecimal) result[0] : BigDecimal.ZERO;
            Long totalOrders = result[1] != null ? ((Number) result[1]).longValue() : 0L;
            Long newCustomersThisMonth = result[2] != null ? ((Number) result[2]).longValue() : 0L;
            
            return new DashboardStatsDTO(totalRevenue, totalOrders, newCustomersThisMonth);
        } catch (Exception e) {
            // Log the error and return default values
            System.err.println("Error fetching dashboard stats: " + e.getMessage());
            e.printStackTrace();
            return new DashboardStatsDTO(BigDecimal.ZERO, 0L, 0L);
        }
    }
}
