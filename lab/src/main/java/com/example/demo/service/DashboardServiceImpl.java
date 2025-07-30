package com.example.demo.service;

import com.example.demo.dto.DashboardStatsDTO;
import com.example.demo.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private DashboardRepository dashboardRepository;

    @Override
    public DashboardStatsDTO getDashboardStats() {
        Object[] result = dashboardRepository.getDashboardStatsRaw();
        double totalRevenue = result[0] != null ? ((Number) result[0]).doubleValue() : 0.0;
        long totalOrders = result[1] != null ? ((Number) result[1]).longValue() : 0L;
        long newCustomersThisMonth = result[2] != null ? ((Number) result[2]).longValue() : 0L;
        return new DashboardStatsDTO(totalRevenue, totalOrders, newCustomersThisMonth);
    }
}
