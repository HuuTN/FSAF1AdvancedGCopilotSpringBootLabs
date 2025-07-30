package com.example.controller;

import com.example.model.dto.OrderInfoDTO;
import com.example.model.dto.OrderRequestDTO;
import com.example.model.entity.Order;
import com.example.model.enums.OrderStatus;
import com.example.service.OrderService;
import com.example.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @Operation(summary = "Get orders by status", description = "Retrieve all orders with a specific status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders with the given status returned successfully")
    })
    @GetMapping("/status/{status}")
    public List<OrderInfoDTO> getOrdersByStatus(@PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrdersByStatus(orderStatus);
            return orderMapper.toOrderInfoDTOs(orders);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }

    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders with user and product details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders returned successfully")
    })
    @GetMapping
    public List<OrderInfoDTO> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return orderMapper.toOrderInfoDTOs(orders);
    }

    @Operation(summary = "Get order by ID", description = "Retrieve order details by order ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found and returned successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public OrderInfoDTO getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return orderMapper.toOrderInfoDTO(order);
    }

    @Operation(summary = "Get orders by user name", description = "Retrieve all orders made by a specific user name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's orders returned successfully")
    })
    @GetMapping("/user/name/{userName}")
    public List<OrderInfoDTO> getOrdersByUser(@PathVariable String userName) {
        List<Order> orders = orderService.getOrdersByUserName(userName);
        return orderMapper.toOrderInfoDTOs(orders);
    }

    @Operation(summary = "Place a new order", description = "Create a new order for a customer by a user with a list of order items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order placed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public OrderInfoDTO placeOrder(@Valid @RequestBody OrderRequestDTO orderRequest) {
        Order order = orderService.placeOrder(orderRequest);
        return orderMapper.toOrderInfoDTO(order);
    }

    @Operation(summary = "Cancel an order", description = "Cancel an existing order by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/cancel")
    public OrderInfoDTO cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        Order order = orderService.getOrderById(id);
        return orderMapper.toOrderInfoDTO(order);
    }
}
