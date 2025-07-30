package com.example.copilot.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private String status;
    private Long userId;
    private List<OrderItemDTO> orderItems;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<OrderItemDTO> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDTO> orderItems) { this.orderItems = orderItems; }
}
