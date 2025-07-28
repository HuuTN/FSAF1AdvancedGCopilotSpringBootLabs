package com.example.usermanagement.dto;

import com.example.usermanagement.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderPostResponse {
    private Long orderId;
    private OrderStatus status;
    private String message;
} 