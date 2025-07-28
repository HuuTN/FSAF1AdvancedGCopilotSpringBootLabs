package com.example.usermanagement.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderPostRequest {
    private Long userId;
    private List<OrderItemRequest> items;
}