package com.example.demo.dto;

import java.util.List;

public class CreateOrderRequestDTO {
    private Long userId;
    private List<OrderItemRequestDTO> items;

    public static class OrderItemRequestDTO {
        private Long productId;
        private Integer quantity;

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public List<OrderItemRequestDTO> getItems() {
        return items;
    }
    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

}