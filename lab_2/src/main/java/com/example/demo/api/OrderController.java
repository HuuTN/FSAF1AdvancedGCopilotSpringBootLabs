package com.example.demo.api;

import com.example.demo.dto.CreateOrderRequestDTO;
import com.example.demo.dto.OrderDTO;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    // Place a new order
    @PostMapping
    public OrderDTO placeOrder(@RequestBody CreateOrderRequestDTO request) {
        Order order = orderService.placeOrder(request);
        return toDTO(order);
    }

    // View all orders (optionally filter by userId)
    @GetMapping
    public List<OrderDTO> getOrders(@RequestParam(required = false) Long userId) {
        List<Order> orders;
        if (userId != null) {
            orders = orderRepository.findAll().stream()
                    .filter(o -> o.getUser() != null && o.getUser().getId().equals(userId))
                    .collect(Collectors.toList());
        } else {
            orders = orderRepository.findAll();
        }
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // View orders by userId (alternative endpoint)
    @GetMapping("/user/{userId}")
    public List<OrderDTO> getOrdersByUser(@PathVariable Long userId) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getUser() != null && o.getUser().getId().equals(userId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Helper method for mapping
    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus().name());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                .map(item -> {
                    com.example.demo.dto.OrderItemDTO itemDTO = new com.example.demo.dto.OrderItemDTO();
                    itemDTO.setId(item.getId());
                    itemDTO.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setPrice(item.getPrice());
                    return itemDTO;
                }).collect(Collectors.toSet()));
        }
        return dto;
    }
}
