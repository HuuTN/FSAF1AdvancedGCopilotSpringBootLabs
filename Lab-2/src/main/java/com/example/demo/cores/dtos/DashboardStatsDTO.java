package com.example.demo.cores.dtos;

import java.math.BigDecimal;

public class DashboardStatsDTO {
    
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long newCustomersThisMonth;
    
    // Constructor for native query projection
    public DashboardStatsDTO(BigDecimal totalRevenue, Long totalOrders, Long newCustomersThisMonth) {
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalOrders = totalOrders != null ? totalOrders : 0L;
        this.newCustomersThisMonth = newCustomersThisMonth != null ? newCustomersThisMonth : 0L;
    }
    
    // Default constructor
    public DashboardStatsDTO() {}
    
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
