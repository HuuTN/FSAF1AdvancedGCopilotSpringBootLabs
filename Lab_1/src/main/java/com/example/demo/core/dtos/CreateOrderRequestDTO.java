package com.example.demo.core.dtos;

import com.example.demo.core.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.HashSet;

public class CreateOrderRequestDTO {
    @NotNull(message = "Order ID cannot be null")
    private Long orderId;

    @NotNull(message = "Order status cannot be null")
    private OrderStatus orderStatus;

    @NotNull(message = "Order items cannot be null")
    private Set<OrderItemsDTO> orderItems = new HashSet<>();

    public CreateOrderRequestDTO() {}

    public CreateOrderRequestDTO(Long orderId, OrderStatus orderStatus, Set<OrderItemsDTO> orderItems) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Set<OrderItemsDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItemsDTO> orderItems) {
        this.orderItems = orderItems;
    }
}

