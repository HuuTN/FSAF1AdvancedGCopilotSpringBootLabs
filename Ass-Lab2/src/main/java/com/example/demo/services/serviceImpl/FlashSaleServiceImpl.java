package com.example.demo.services.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.cores.dtos.ReservationResponseDTO;
import com.example.demo.cores.entity.FlashSale;
import com.example.demo.cores.entity.FlashSaleReservation;
import com.example.demo.cores.enums.ReservationStatus;
import com.example.demo.cores.repository.FlashSaleRepository;
import com.example.demo.cores.repository.FlashSaleReservationRepository;
import com.example.demo.exception.FlashSaleNotActiveException;
import com.example.demo.exception.FlashSaleNotFoundException;
import com.example.demo.exception.FlashSaleSoldOutException;
import com.example.demo.services.service.FlashSaleService;

@Service
public class FlashSaleServiceImpl implements FlashSaleService {
    
    @Autowired
    private FlashSaleRepository flashSaleRepository;
    
    @Autowired
    private FlashSaleReservationRepository flashSaleReservationRepository;
    
    @Override
    @Transactional
    public ReservationResponseDTO createReservation(Long userId, Long saleId) {
        // Step 1: Get flash sale with pessimistic lock
        FlashSale flashSale = getFlashSaleWithLock(saleId);
        
        // Step 2: Validate the flash sale is active
        LocalDateTime now = LocalDateTime.now();
        validateFlashSaleActive(flashSale, now);
        
        // Step 3: Validate inventory availability
        validateInventoryAvailable(saleId, flashSale);
        
        // Step 4: Create and save the reservation
        FlashSaleReservation savedReservation = createAndSaveReservation(flashSale, userId, now);
        
        // Step 5: Build and return response
        return buildResponseDTO(saleId, savedReservation);
    }
    
    /**
     * Retrieves a flash sale with pessimistic lock to prevent concurrent modifications
     */
    private FlashSale getFlashSaleWithLock(Long saleId) {
        return flashSaleRepository.findByIdWithLock(saleId)
                .orElseThrow(() -> new FlashSaleNotFoundException("Flash sale not found with ID: " + saleId));
    }
    
    /**
     * Validates that the flash sale is currently active
     */
    private void validateFlashSaleActive(FlashSale flashSale, LocalDateTime now) {
        if (now.isBefore(flashSale.getStartTime()) || now.isAfter(flashSale.getEndTime())) {
            throw new FlashSaleNotActiveException("Flash sale is not active. Sale period: " 
                + flashSale.getStartTime() + " to " + flashSale.getEndTime());
        }
    }
    
    /**
     * Validates that there is inventory available for the flash sale
     */
    private void validateInventoryAvailable(Long saleId, FlashSale flashSale) {
        long currentReservations = flashSaleReservationRepository.countByFlashSaleIdAndStatus(
                saleId, ReservationStatus.PENDING);
                
        if (currentReservations >= flashSale.getTotalQuantity()) {
            throw new FlashSaleSoldOutException("Flash sale is sold out. Available quantity: " 
                + flashSale.getTotalQuantity() + ", Current reservations: " + currentReservations);
        }
    }
    
    /**
     * Creates and saves a new reservation
     */
    private FlashSaleReservation createAndSaveReservation(FlashSale flashSale, Long userId, LocalDateTime now) {
        FlashSaleReservation reservation = new FlashSaleReservation();
        reservation.setFlashSale(flashSale);
        reservation.setUserId(userId);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setExpireTime(now.plusMinutes(10));
        
        return flashSaleReservationRepository.save(reservation);
    }
    
    /**
     * Builds the response DTO
     */
    private ReservationResponseDTO buildResponseDTO(Long saleId, FlashSaleReservation savedReservation) {
        return new ReservationResponseDTO(saleId, savedReservation.getExpireTime());
    }
}
