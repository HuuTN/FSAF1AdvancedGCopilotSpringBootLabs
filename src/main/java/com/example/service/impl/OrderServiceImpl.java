package com.example.service.impl;

import com.example.dto.CreateOrderRequestDTO;
import com.example.dto.OrderDTO;
import com.example.dto.OrderItemDTO;
import com.example.entity.Order;
import com.example.entity.OrderItem;
import com.example.entity.Product;
import com.example.entity.User;
import com.example.entity.OrderStatus;
import com.example.repository.OrderRepository;
import com.example.repository.OrderItemRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public OrderDTO placeOrder(CreateOrderRequestDTO request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow();
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingAddress(request.getShippingAddress());
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequestDTO.OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).orElseThrow();
            if (product.getStock() < itemReq.getQuantity()) {
                throw new RuntimeException("InsufficientStockException");
            }
            product.setStock(product.getStock() - itemReq.getQuantity());
            productRepository.save(product);
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(product.getPrice());
            items.add(item);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
        }
        order.setTotalAmount(total);
        orderRepository.save(order);
        for (OrderItem item : items) {
            orderItemRepository.save(item);
        }
        order.getItems().addAll(items);
        return toDTO(order);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        // Implement as needed
        return new ArrayList<>();
    }

    @Override
    public List<OrderDTO> getOrdersByUser(Long userId) {
        // Implement as needed
        return new ArrayList<>();
    }

    @Override
    public void cancelOrder(Long orderId) {
        // Implement as needed
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setPrice(item.getPrice());
            itemDTOs.add(itemDTO);
        }
        dto.setItems(itemDTOs);
        return dto;
    }
}
