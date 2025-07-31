package com.example.demo.cores.dtos;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class FlashSaleDTO {
    
    private Long id;
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    private String productName;
    
    @NotNull(message = "Total quantity is required")
    private Integer totalQuantity;
    
    private Integer remainingQuantity;
    
    @NotNull(message = "Flash sale price is required")
    private Double flashSalePrice;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalDateTime endTime;
    
    // Constructors
    public FlashSaleDTO() {}
    
    public FlashSaleDTO(Long id, Long productId, String productName, Integer totalQuantity, 
                       Integer remainingQuantity, Double flashSalePrice, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.remainingQuantity = remainingQuantity;
        this.flashSalePrice = flashSalePrice;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public Integer getRemainingQuantity() {
        return remainingQuantity;
    }
    
    public void setRemainingQuantity(Integer remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }
    
    public Double getFlashSalePrice() {
        return flashSalePrice;
    }
    
    public void setFlashSalePrice(Double flashSalePrice) {
        this.flashSalePrice = flashSalePrice;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
