package com.example.demo.cores.repository;

import com.example.demo.cores.entity.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT fs FROM FlashSale fs WHERE fs.id = :saleId")
    Optional<FlashSale> findByIdWithLock(@Param("saleId") Long saleId);
    
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime <= :now AND fs.endTime >= :now")
    List<FlashSale> findActiveFlashSales(@Param("now") LocalDateTime now);
    
    @Query("SELECT fs FROM FlashSale fs WHERE fs.product.id = :productId AND fs.startTime <= :now AND fs.endTime >= :now")
    List<FlashSale> findActiveFlashSalesByProductId(@Param("productId") Long productId, @Param("now") LocalDateTime now);
    
    @Query("SELECT fs FROM FlashSale fs WHERE fs.startTime > :now")
    List<FlashSale> findUpcomingFlashSales(@Param("now") LocalDateTime now);
}
