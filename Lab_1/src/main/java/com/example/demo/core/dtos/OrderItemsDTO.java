package com.example.demo.core.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemsDTO {
    @NotNull(message = "Order item id cannot be null")
    private Long id;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private Double price;

    @NotNull(message = "Product ID is required")
    private Long productId;

    public OrderItemsDTO() {}

    public OrderItemsDTO(Long id, Integer quantity, Double price, Long productId) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.productId = productId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
