package com.example.service.impl;

import com.example.dto.DashboardStatsDTO;
import com.example.repository.OrderRepository;
import com.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        Object[] result = orderRepository.getDashboardStats();
        
        if (result == null || result.length < 3) {
            // Return default values if no data found
            return new DashboardStatsDTO(BigDecimal.ZERO, 0L, 0L);
        }
        
        BigDecimal totalRevenue = (BigDecimal) result[0];
        Long totalOrders = ((Number) result[1]).longValue();
        Long newCustomersThisMonth = ((Number) result[2]).longValue();
        
        return new DashboardStatsDTO(
            totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
            totalOrders,
            newCustomersThisMonth
        );
    }
}
