package com.example.model.dto;

import com.example.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDTO {
    private Long orderId;
    private String customerName;
    private String customerEmail;
    private String orderedBy; // This can be derived from customer name
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<ProductInfo> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private Long productId;
        private String productName;
        private String categoryName;
        private int quantity;
        private String price;
    }
}
