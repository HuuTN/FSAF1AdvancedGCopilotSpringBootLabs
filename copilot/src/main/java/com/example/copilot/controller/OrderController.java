package com.example.copilot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.copilot.dto.CreateOrderRequestDTO;
import com.example.copilot.dto.OrderDTO;
import com.example.copilot.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // POST /api/orders - Place a new order
    @PostMapping
    public OrderDTO placeOrder(@RequestBody CreateOrderRequestDTO request) {
        return orderService.placeOrder(request);
    }

    // GET /api/orders?userId=... - View a user's order history or all orders
    @GetMapping
    public List<OrderDTO> getOrders(@RequestParam(required = false) Long userId) {
        if (userId != null) {
            return orderService.getOrdersByUserId(userId);
        }
        return orderService.getAllOrders();
    }

    // GET /api/users/{userId}/orders - View a user's order history
    @GetMapping("/users/{userId}/orders")
    public List<OrderDTO> getOrdersByUserId(@PathVariable Long userId) {
        return orderService.getOrdersByUserId(userId);
    }
}
