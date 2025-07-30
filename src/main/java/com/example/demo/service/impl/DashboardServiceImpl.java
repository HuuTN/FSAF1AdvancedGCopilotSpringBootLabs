package com.example.demo.service.impl;

import java.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.*;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.DashboardService;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {
    
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public DashboardServiceImpl(OrderRepository orderRepository,
                             UserRepository userRepository,
                             ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDTO getDashboardStats() {
        return orderRepository.getDashboardStats();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public DashboardDataDTO getDetailedDashboardData(int topProductsLimit, int recentOrdersLimit) {
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime tomorrow = today.plusDays(1);
        
        // Get general stats
        DashboardStatsDTO stats = getDashboardStats();
        
        // Get top products
        List<TopProductDTO> topProducts = orderRepository.getTopSellingProducts(PageRequest.of(0, topProductsLimit));
        
        // Get recent orders
        List<RecentOrderDTO> recentOrders = orderRepository.getRecentOrders(PageRequest.of(0, recentOrdersLimit));
        List<OrderStatusCount> ordersByStatus = orderRepository.getOrderCountByStatus();
        Double todayRevenue = orderRepository.getTotalRevenue(today, tomorrow);

        return new DashboardDataDTO(
            stats,
            topProducts,
            recentOrders,
            ordersByStatus,
            todayRevenue
        );
    }
}
