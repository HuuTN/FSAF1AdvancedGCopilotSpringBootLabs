package com.example.demo.dto;

import java.time.LocalDateTime;

public class RecentOrderDTO {
    private Long orderId;
    private LocalDateTime orderDate;
    private String status;
    private String customerName;
    private Long totalItems;
    private Double totalAmount;

    public RecentOrderDTO(Long orderId, LocalDateTime orderDate, String status, 
                         String customerName, Long totalItems, Double totalAmount) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.customerName = customerName;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
    }

    // Getters
    public Long getOrderId() {
        return orderId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }
}
