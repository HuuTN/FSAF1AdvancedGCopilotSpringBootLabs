package com.example.demo.dto;

public class DashboardStatsDTO {
    private double totalRevenue;
    private long totalOrders;
    private long newCustomersThisMonth;

    public DashboardStatsDTO(double totalRevenue, long totalOrders, long newCustomersThisMonth) {
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.newCustomersThisMonth = newCustomersThisMonth;
    }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public long getNewCustomersThisMonth() { return newCustomersThisMonth; }
    public void setNewCustomersThisMonth(long newCustomersThisMonth) { this.newCustomersThisMonth = newCustomersThisMonth; }
}
