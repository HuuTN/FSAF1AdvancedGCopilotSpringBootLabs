package com.example.model.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for dashboard statistics.
 * Contains key metrics for the dashboard view.
 */
public class DashboardStatsDTO {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long newCustomersThisMonth;

    // Default constructor
    public DashboardStatsDTO() {
    }

    // Constructor with all fields
    public DashboardStatsDTO(BigDecimal totalRevenue, Long totalOrders, Long newCustomersThisMonth) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.newCustomersThisMonth = newCustomersThisMonth;
    }

    // Getters and Setters
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getNewCustomersThisMonth() {
        return newCustomersThisMonth;
    }

    public void setNewCustomersThisMonth(Long newCustomersThisMonth) {
        this.newCustomersThisMonth = newCustomersThisMonth;
    }

    @Override
    public String toString() {
        return "DashboardStatsDTO{" +
                "totalRevenue=" + totalRevenue +
                ", totalOrders=" + totalOrders +
                ", newCustomersThisMonth=" + newCustomersThisMonth +
                '}';
    }
}
