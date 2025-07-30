package com.example.model.dto;

import java.util.List;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;

public class OrderRequestDTO {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemDTO> orderItems;

    // Default constructor
    public OrderRequestDTO() {
    }

    // Constructor with parameters
    public OrderRequestDTO(Long userId, Long customerId, List<OrderItemDTO> orderItems) {
        this.userId = userId;
        this.customerId = customerId;
        this.orderItems = orderItems;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
}
