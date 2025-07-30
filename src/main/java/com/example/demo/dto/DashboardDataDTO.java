package com.example.demo.dto;

import java.util.List;

public class DashboardDataDTO {
    private final DashboardStatsDTO stats;
    private final List<TopProductDTO> topProducts;
    private final List<RecentOrderDTO> recentOrders;
    private final List<OrderStatusCount> ordersByStatus;
    private final Double todayRevenue;

    public DashboardDataDTO(
            DashboardStatsDTO stats,
            List<TopProductDTO> topProducts,
            List<RecentOrderDTO> recentOrders,
            List<OrderStatusCount> ordersByStatus,
            Double todayRevenue) {
        this.stats = stats;
        this.topProducts = topProducts;
        this.recentOrders = recentOrders;
        this.ordersByStatus = ordersByStatus;
        this.todayRevenue = todayRevenue != null ? todayRevenue : 0.0;
    }

    public DashboardStatsDTO getStats() {
        return stats;
    }

    public List<TopProductDTO> getTopProducts() {
        return topProducts;
    }

    public List<RecentOrderDTO> getRecentOrders() {
        return recentOrders;
    }

    public List<OrderStatusCount> getOrdersByStatus() {
        return ordersByStatus;
    }

    public Double getTodayRevenue() {
        return todayRevenue;
    }
}
