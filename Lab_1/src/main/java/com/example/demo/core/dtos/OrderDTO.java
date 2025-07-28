package com.example.demo.core.dtos;

import com.example.demo.core.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class OrderDTO {
    @NotNull(message = "Order id cannot be null")
    private Long id;

    @NotNull(message = "Order status is required")
    private OrderStatus status;

    public OrderDTO() {}

    public OrderDTO(Long id, OrderStatus status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
