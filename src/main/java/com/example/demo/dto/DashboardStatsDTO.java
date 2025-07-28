package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Double totalRevenue;
    private Long totalOrders;
    private Long newCustomersThisMonth;

    // Getters
    public Double getTotalRevenue() {
        return totalRevenue != null ? totalRevenue : 0.0;
    }

    public Long getTotalOrders() {
        return totalOrders != null ? totalOrders : 0L;
    }

    public Long getNewCustomersThisMonth() {
        return newCustomersThisMonth != null ? newCustomersThisMonth : 0L;
    }

        // Static factory method for creating instances with default values
    public static DashboardStatsDTO createEmpty() {
        return DashboardStatsDTO.builder()
                .totalRevenue(0.0)
                .totalOrders(0L)
                .newCustomersThisMonth(0L)
                .build();
    }
}
