package com.example.demo.apis.controller;

import com.example.demo.core.dtos.OrderItemsDTO;
import com.example.demo.services.service.OrderItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/order-items")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @PostMapping
    @Operation(summary = "Create OrderItem", description = "Creates a new order item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OrderItem created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<OrderItemsDTO> createOrderItem(@Valid @RequestBody OrderItemsDTO orderItemDTO) {
        OrderItemsDTO created = orderItemService.createOrderItem(orderItemDTO);
        if (created == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get OrderItem by ID", description = "Returns an order item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OrderItem found and returned"),
        @ApiResponse(responseCode = "404", description = "OrderItem not found")
    })
    public ResponseEntity<OrderItemsDTO> getOrderItemById(@PathVariable("id") Long id) {
        Optional<OrderItemsDTO> result = orderItemService.getOrderItemById(id);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all OrderItems", description = "Returns a list of all order items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of order items returned successfully")
    })
    public ResponseEntity<List<OrderItemsDTO>> getAllOrderItems() {
        List<OrderItemsDTO> items = orderItemService.getAllOrderItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/page")
    @Operation(summary = "Get paged OrderItems", description = "Returns a paginated list of order items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paged order items returned successfully")
    })
    public ResponseEntity<Page<OrderItemsDTO>> getAllOrderItemsPage(Pageable pageable) {
        Page<OrderItemsDTO> page = orderItemService.getAllOrderItems(pageable);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update OrderItem", description = "Updates an order item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OrderItem updated successfully"),
        @ApiResponse(responseCode = "404", description = "OrderItem not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<OrderItemsDTO> updateOrderItem(@PathVariable("id") Long id, @Valid @RequestBody OrderItemsDTO orderItemDTO) {
        OrderItemsDTO updated = orderItemService.updateOrderItem(id, orderItemDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete OrderItem", description = "Deletes an order item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "OrderItem deleted successfully"),
        @ApiResponse(responseCode = "404", description = "OrderItem not found")
    })
    public ResponseEntity<Void> deleteOrderItem(@PathVariable("id") Long id) {
        boolean deleted = orderItemService.deleteOrderItem(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
