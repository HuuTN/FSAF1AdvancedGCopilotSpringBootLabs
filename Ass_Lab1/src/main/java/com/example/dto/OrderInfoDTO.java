package com.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderInfoDTO {
    public OrderInfoDTO(Long orderId, String customerName, String customerEmail, String orderedBy, BigDecimal totalAmount, String status, List<ProductInfo> products) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.orderedBy = orderedBy;
        this.totalAmount = totalAmount;
        this.status = status;
        this.products = products;
    }
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String orderedBy;
    private BigDecimal totalAmount;
    private String status;
    private List<ProductInfo> products;

    @Data
    @NoArgsConstructor
    public static class ProductInfo {
        public ProductInfo(Long productId, String productName, String categoryName, int quantity, String price) {
            this.productId = productId;
            this.productName = productName;
            this.categoryName = categoryName;
            this.quantity = quantity;
            this.price = price;
        }
        private Long productId;
        private String productName;
        private String categoryName;
        private int quantity;
        private String price;
    }
}
