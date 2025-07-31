package com.example.demo.services.service;

import com.example.demo.cores.dtos.ReservationResponseDTO;

public interface FlashSaleService {
    
    ReservationResponseDTO createReservation(Long userId, Long saleId);
}
