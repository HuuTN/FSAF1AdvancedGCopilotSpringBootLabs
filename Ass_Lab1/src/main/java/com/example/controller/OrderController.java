package com.example.controller;

import com.example.dto.OrderInfoDTO;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.entity.Product;
import com.example.entity.User;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Get orders by status", description = "Retrieve all orders with a specific status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders with the given status returned successfully")
    })
    @GetMapping("/status/{status}")
    public List<OrderInfoDTO> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return orders.stream().map(this::toOrderInfoDTO).collect(Collectors.toList());
    }

    @Operation(summary = "Get all orders", description = "Retrieve a list of all orders with user and product details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders returned successfully")
    })
    @GetMapping
    public List<OrderInfoDTO> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return orders.stream().map(this::toOrderInfoDTO).collect(Collectors.toList());
    }

    @Operation(summary = "Get order by ID", description = "Retrieve order details by order ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found and returned successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public OrderInfoDTO getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return toOrderInfoDTO(order);
    }

    @Operation(summary = "Get orders by user name", description = "Retrieve all orders made by a specific user name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's orders returned successfully")
    })
    @GetMapping("/user/name/{userName}")
    public List<OrderInfoDTO> getOrdersByUser(@PathVariable String userName) {
        List<Order> orders = orderService.getOrdersByUserName(userName);
        return orders.stream().map(this::toOrderInfoDTO).collect(Collectors.toList());
    }

    private OrderInfoDTO toOrderInfoDTO(Order order) {
        if (order == null)
            return null;
        User user = order.getUser();
        List<OrderInfoDTO.ProductInfo> products = order.getItems().stream().map(this::toProductInfo)
                .collect(Collectors.toList());
        return new OrderInfoDTO(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                user != null ? user.getName() : null,
                order.getTotalAmount(),
                order.getStatus(),
                products);
    }

    private OrderInfoDTO.ProductInfo toProductInfo(OrderItem item) {
        Product product = item.getProduct();
        return new OrderInfoDTO.ProductInfo(
                product.getId(),
                product.getName(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                item.getQuantity(),
                item.getPrice() != null ? item.getPrice().toString() : null);
    }
}
