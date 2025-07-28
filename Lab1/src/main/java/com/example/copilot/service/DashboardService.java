package com.example.copilot.service;

import com.example.copilot.core.dto.DashboardStatsDTO;

/**
 * Dashboard Service Interface
 * Provides dashboard statistics with optimized database queries
 */
public interface DashboardService {
    
    /**
     * Get dashboard statistics using single efficient database query
     * @return DashboardStatsDTO containing total revenue, orders, and new customers
     */
    DashboardStatsDTO getDashboardStats();
}
