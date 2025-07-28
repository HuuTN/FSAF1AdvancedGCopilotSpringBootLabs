package com.example.demo.core.dtos;

import java.math.BigDecimal;

/**
 * Interface projection for dashboard statistics query results
 * Used with native SQL queries to fetch multiple aggregated statistics
 */
public interface DashboardStatsDTO {
    BigDecimal getTotalRevenue();
    Long getTotalOrders();
    Long getNewCustomersThisMonth();
}
