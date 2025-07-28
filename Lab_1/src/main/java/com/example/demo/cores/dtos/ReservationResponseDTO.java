package com.example.demo.cores.dtos;

import java.time.LocalDateTime;

public class ReservationResponseDTO {
    
    private Long saleId;
    private LocalDateTime expireTime;
    
    // Constructors
    public ReservationResponseDTO() {}
    
    public ReservationResponseDTO(Long saleId, LocalDateTime expireTime) {
        this.saleId = saleId;
        this.expireTime = expireTime;
    }
    
    // Getters and Setters
    public Long getSaleId() {
        return saleId;
    }
    
    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }
    
    public LocalDateTime getExpireTime() {
        return expireTime;
    }
    
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}
