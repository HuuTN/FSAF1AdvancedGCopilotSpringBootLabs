package com.example.service;

import com.example.model.dto.DashboardStatsDTO;

/**
 * Service interface for dashboard operations.
 * Provides methods to retrieve dashboard statistics and metrics.
 */
public interface DashboardService {

    /**
     * Get comprehensive dashboard statistics.
     * 
     * @return DashboardStatsDTO containing total revenue, total orders, and new
     *         customers this month
     */
    DashboardStatsDTO getDashboardStats();
}
