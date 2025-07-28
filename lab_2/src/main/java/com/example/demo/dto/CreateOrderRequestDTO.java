package com.example.demo.dto;

import java.util.Set;

public class CreateOrderRequestDTO {
    private Long userId;
    private Set<OrderItemRequest> items;

    public static class OrderItemRequest {
        private Long productId;
        private Integer quantity;
        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Set<OrderItemRequest> getItems() { return items; }
    public void setItems(Set<OrderItemRequest> items) { this.items = items; }
}
