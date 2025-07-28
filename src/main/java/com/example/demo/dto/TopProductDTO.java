package com.example.demo.dto;

public class TopProductDTO {
    private Long productId;
    private String productName;
    private Long totalQuantity;
    private Double totalRevenue;

    public TopProductDTO(Long productId, String productName, Long totalQuantity, Double totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    // Getters
    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Long getTotalQuantity() {
        return totalQuantity;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }
}
