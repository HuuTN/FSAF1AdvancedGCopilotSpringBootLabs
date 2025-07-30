package com.example.demo.cores.repository;

import com.example.demo.cores.entity.FlashSaleReservation;
import com.example.demo.cores.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlashSaleReservationRepository extends JpaRepository<FlashSaleReservation, Long> {
    
    @Query("SELECT COUNT(fsr) FROM FlashSaleReservation fsr WHERE fsr.flashSale.id = :saleId AND fsr.status = :status")
    long countByFlashSaleIdAndStatus(@Param("saleId") Long saleId, @Param("status") ReservationStatus status);
    
    @Query("SELECT fsr FROM FlashSaleReservation fsr WHERE fsr.userId = :userId AND fsr.flashSale.id = :flashSaleId")
    List<FlashSaleReservation> findByUserIdAndFlashSaleId(@Param("userId") Long userId, @Param("flashSaleId") Long flashSaleId);
    
    @Query("SELECT fsr FROM FlashSaleReservation fsr WHERE fsr.expireTime <= :now AND fsr.status = :status")
    List<FlashSaleReservation> findExpiredReservations(@Param("now") LocalDateTime now, @Param("status") ReservationStatus status);
    
    List<FlashSaleReservation> findByFlashSaleIdAndStatus(Long flashSaleId, ReservationStatus status);
    
    List<FlashSaleReservation> findByUserId(Long userId);
}
