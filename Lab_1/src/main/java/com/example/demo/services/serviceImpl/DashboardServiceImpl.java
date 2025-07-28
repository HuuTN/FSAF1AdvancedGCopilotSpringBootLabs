package com.example.demo.services.serviceImpl;

import com.example.demo.cores.dtos.DashboardStatsDTO;
import com.example.demo.cores.repository.OrderRepository;
import com.example.demo.services.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public DashboardStatsDTO getDashboardStats() {
        Object[] result = orderRepository.getDashboardStats();
        // Defensive: result[0]=totalRevenue, result[1]=totalOrders, result[2]=newCustomersThisMonth
        BigDecimal totalRevenue = result[0] != null ? new BigDecimal(result[0].toString()) : BigDecimal.ZERO;
        Long totalOrders = result[1] != null ? Long.valueOf(result[1].toString()) : 0L;
        Long newCustomersThisMonth = result[2] != null ? Long.valueOf(result[2].toString()) : 0L;
        return new DashboardStatsDTO(totalRevenue, totalOrders, newCustomersThisMonth);
    }
}
