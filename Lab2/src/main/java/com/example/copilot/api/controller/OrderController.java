package com.example.copilot.api.controller;

import com.example.copilot.core.dto.OrderDTO;
import com.example.copilot.service.OrderService;
import com.example.copilot.core.dto.CreateOrderRequestDTO;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public OrderDTO placeOrder(@Valid @RequestBody CreateOrderRequestDTO request) {
        return orderService.placeOrder(request);
    }

    @GetMapping("/{id}")
    public OrderDTO getById(@PathVariable Long id) {
        return orderService.getById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @GetMapping
    public List<OrderDTO> getAll() {
        return orderService.getAll();
    }

    @GetMapping("/user/{userId}")
    public List<OrderDTO> getByUserId(@PathVariable Long userId) {
        return orderService.getByUserId(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        if (!orderService.delete(id)) throw new RuntimeException("Order not found");
    }
}
