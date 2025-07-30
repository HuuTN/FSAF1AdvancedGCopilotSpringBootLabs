package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.domain.OrderStatus;
import com.example.demo.entity.Order;

public class OrderDTO {

    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Long userId;
    private List<OrderItemDTO> orderItems;

    // convert order entity to DTO
    public static OrderDTO fromEntity(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setOrderDate(order.getOrderDate());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setOrderItems(order.getOrderItems().stream()
            .map(OrderItemDTO::fromEntity)
            .collect(Collectors.toList()));
        return orderDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

}