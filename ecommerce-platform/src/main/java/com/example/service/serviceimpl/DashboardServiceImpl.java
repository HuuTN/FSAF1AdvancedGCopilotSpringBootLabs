package com.example.service.serviceimpl;

import com.example.model.dto.DashboardStatsDTO;
import com.example.repository.OrderRepository;
import com.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implementation of DashboardService.
 * Provides dashboard statistics and metrics.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;

    @Autowired
    public DashboardServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        Object[] stats = orderRepository.getDashboardStats();

        // Extract values from the query result
        BigDecimal totalRevenue = stats[0] != null ? (BigDecimal) stats[0] : BigDecimal.ZERO;
        Long totalOrders = stats[1] != null ? ((Number) stats[1]).longValue() : 0L;
        Long newCustomersThisMonth = stats[2] != null ? ((Number) stats[2]).longValue() : 0L;

        return new DashboardStatsDTO(totalRevenue, totalOrders, newCustomersThisMonth);
    }
}
