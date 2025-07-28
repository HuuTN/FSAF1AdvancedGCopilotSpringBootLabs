package com.example.demo.services.service;

import com.example.demo.cores.dtos.DashboardStatsDTO;

public interface DashboardService {
    
    /**
     * Get dashboard statistics including total revenue, total orders, and new customers this month
     * @return DashboardStatsDTO containing all dashboard statistics
     */
    DashboardStatsDTO getDashboardStats();
}
