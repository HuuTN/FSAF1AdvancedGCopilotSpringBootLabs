package com.example.demo.dto;

import java.time.LocalDate;

public class DailyRevenueDTO {
    private LocalDate date;
    private Long orderCount;
    private Double totalRevenue;

    public DailyRevenueDTO(LocalDate date, Long orderCount, Double totalRevenue) {
        this.date = date;
        this.orderCount = orderCount;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public Long getOrderCount() {
        return orderCount;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }
}
