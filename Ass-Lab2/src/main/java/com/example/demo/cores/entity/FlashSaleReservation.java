package com.example.demo.cores.entity;

import com.example.demo.cores.enums.ReservationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flash_sale_reservations")
public class FlashSaleReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id", nullable = false)
    private FlashSale flashSale;
    
    @Column(nullable = false)
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
    
    @Column(nullable = false)
    private LocalDateTime expireTime;
    
    // Constructors
    public FlashSaleReservation() {}
    
    public FlashSaleReservation(Long id, FlashSale flashSale, Long userId, ReservationStatus status, LocalDateTime expireTime) {
        this.id = id;
        this.flashSale = flashSale;
        this.userId = userId;
        this.status = status;
        this.expireTime = expireTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public FlashSale getFlashSale() {
        return flashSale;
    }
    
    public void setFlashSale(FlashSale flashSale) {
        this.flashSale = flashSale;
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
}
