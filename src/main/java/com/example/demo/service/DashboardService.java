package com.example.demo.service;

import com.example.demo.dto.DashboardDataDTO;
import com.example.demo.dto.DashboardStatsDTO;

public interface DashboardService {
    /**
     * Get comprehensive dashboard statistics including:
     * - Total revenue from delivered orders
     * - Total number of orders
     * - New customers this month
     */
    DashboardStatsDTO getDashboardStats();

    /**
     * Get detailed dashboard data including:
     * - Overall statistics
     * - Top selling products
     * - Recent orders
     * - Order counts by status
     * - Today's revenue
     * @param topProductsLimit maximum number of top products to return
     * @param recentOrdersLimit maximum number of recent orders to return
     */
    DashboardDataDTO getDetailedDashboardData(int topProductsLimit, int recentOrdersLimit);
        
}
