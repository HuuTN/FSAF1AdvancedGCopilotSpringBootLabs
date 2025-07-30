
package com.example.demo.apis.controller;

import com.example.demo.core.entity.Order;
import com.example.demo.services.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    @Operation(summary = "Get orders by user ID", description = "Returns all orders for a given user ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of orders for user returned successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        if (orders == null || orders.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "User not found or user has no orders");
            error.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(orders);
    }
    @Autowired
    private OrderService orderService;

    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of orders returned successfully")
    })
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Operation(summary = "Get order by ID", description = "Returns an order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found and returned"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Order not found");
            error.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new order", description = "Creates a new order with the provided information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @Operation(summary = "Update an existing order", description = "Updates the order with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order updated successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        Optional<Order> updatedOrder = orderService.updateOrder(id, orderDetails);
        if (updatedOrder.isPresent()) {
            return ResponseEntity.ok(updatedOrder.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Order not found");
            error.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete an order", description = "Deletes the order with the given ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        boolean deleted = orderService.deleteOrder(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Order not found");
            error.put("status", HttpStatus.NOT_FOUND.value());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
    }
}
