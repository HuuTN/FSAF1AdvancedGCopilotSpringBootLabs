package com.example.demo.cores.dtos;

import com.example.demo.cores.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class FlashSaleReservationDTO {
    
    private Long id;
    
    @NotNull(message = "Flash sale ID is required")
    private Long flashSaleId;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Status is required")
    private ReservationStatus status;
    
    @NotNull(message = "Expire time is required")
    private LocalDateTime expireTime;
    
    // Additional fields for display
    private String productName;
    private Double flashSalePrice;
    
    // Constructors
    public FlashSaleReservationDTO() {}
    
    public FlashSaleReservationDTO(Long id, Long flashSaleId, Long userId, ReservationStatus status, 
                                 LocalDateTime expireTime, String productName, Double flashSalePrice) {
        this.id = id;
        this.flashSaleId = flashSaleId;
        this.userId = userId;
        this.status = status;
        this.expireTime = expireTime;
        this.productName = productName;
        this.flashSalePrice = flashSalePrice;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getFlashSaleId() {
        return flashSaleId;
    }
    
    public void setFlashSaleId(Long flashSaleId) {
        this.flashSaleId = flashSaleId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Double getFlashSalePrice() {
        return flashSalePrice;
    }
    
    public void setFlashSalePrice(Double flashSalePrice) {
        this.flashSalePrice = flashSalePrice;
    }
}
