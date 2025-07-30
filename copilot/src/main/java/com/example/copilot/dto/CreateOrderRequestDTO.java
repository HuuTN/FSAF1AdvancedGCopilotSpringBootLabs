package com.example.copilot.dto;

import java.util.List;

public class CreateOrderRequestDTO {
    private Long userId;
    private List<CreateOrderItemDTO> items;

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<CreateOrderItemDTO> getItems() { return items; }
    public void setItems(List<CreateOrderItemDTO> items) { this.items = items; }

    public static class CreateOrderItemDTO {
        private Long productId;
        private Integer quantity;
        // Getters and setters
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
