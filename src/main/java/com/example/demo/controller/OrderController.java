package com.example.demo.controller;

import com.example.demo.dto.CreateOrderRequestDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.service.OrderService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
public class OrderController {

@Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody CreateOrderRequestDTO request) {
        OrderDTO order = orderService.placeOrder(request);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long userId) {
        List<OrderDTO> userOrders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(userOrders);
    }

}
