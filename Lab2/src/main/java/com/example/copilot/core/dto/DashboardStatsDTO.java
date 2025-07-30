package com.example.copilot.core.dto;

import java.math.BigDecimal;

public class DashboardStatsDTO {
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long newCustomersThisMonth;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(BigDecimal totalRevenue, Long totalOrders, Long newCustomersThisMonth) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.newCustomersThisMonth = newCustomersThisMonth;
    }

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
