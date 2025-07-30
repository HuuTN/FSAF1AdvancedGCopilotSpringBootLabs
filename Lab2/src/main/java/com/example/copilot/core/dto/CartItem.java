package com.example.copilot.core.dto;

/**
 * DTO representing an item in a shopping cart for order placement
 */
public class CartItem {
    private Long productId;
    private Integer quantity;
    
    // Constructors
    public CartItem() {}
    
    public CartItem(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
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
