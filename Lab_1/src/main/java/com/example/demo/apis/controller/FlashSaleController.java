package com.example.demo.apis.controller;

import com.example.demo.cores.dtos.ReservationResponseDTO;
import com.example.demo.services.service.FlashSaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/flash-sales")
@Tag(name = "Flash Sale Management", description = "APIs for managing flash sale reservations")
public class FlashSaleController {
    
    @Autowired
    private FlashSaleService flashSaleService;
    
    @PostMapping("/{saleId}/reservations")
    @Operation(
        summary = "Create Flash Sale Reservation", 
        description = "Create a new reservation for a flash sale. This operation uses pessimistic locking to handle concurrency and prevent overselling."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "404", description = "Flash sale not found"),
        @ApiResponse(responseCode = "409", description = "Flash sale not active or sold out"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Parameter(description = "Flash sale ID", required = true, example = "1")
            @PathVariable @NotNull Long saleId,
            
            @Parameter(description = "User ID for the reservation", required = true, example = "123")
            @RequestParam @NotNull Long userId) {
        
        ReservationResponseDTO reservation = flashSaleService.createReservation(userId, saleId);
        return new ResponseEntity<>(reservation, HttpStatus.CREATED);
    }
}
