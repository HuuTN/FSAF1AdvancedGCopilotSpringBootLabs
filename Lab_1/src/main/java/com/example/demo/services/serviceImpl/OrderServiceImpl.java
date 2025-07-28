
package com.example.demo.services.serviceImpl;

import java.util.HashSet;

import com.example.demo.core.dtos.DashboardStatsDTO;
import com.example.demo.core.entity.Order;
import com.example.demo.core.entity.OrderItem;
import com.example.demo.core.entity.Product;
import com.example.demo.core.repository.OrderRepository;
import com.example.demo.core.repository.OrderItemRepository;
import com.example.demo.core.repository.ProductRepository;
import com.example.demo.core.dtos.OrderItemsDTO;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.services.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderServiceImpl implements  OrderService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> updateOrder(Long id, Order orderDetails) {
        return orderRepository.findById(id).map(order -> {
            order.setOrderItems(orderDetails.getOrderItems());
            order.setUser(orderDetails.getUser());
            return orderRepository.save(order);
        });
    }

    @Override
    public boolean deleteOrder(Long id) {
        return orderRepository.findById(id).map(order -> {
            orderRepository.delete(order);
            return true;
        }).orElse(false);
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findOrdersByUserIdNative(userId);
    }

    @Transactional
    @Override
    public Order placeOrder(com.example.demo.core.dtos.CreateOrderRequestDTO request) {
        // Step a: Create and save a new Order entity
        Order order = new Order();
        order.setId(request.getOrderId());
        order.setStatus(request.getOrderStatus());

        // Step b-e: Process each item
        Set<OrderItem> orderItems = new HashSet<>();
        for (OrderItemsDTO itemDTO : request.getOrderItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new InsufficientStockException("Product not found: " + itemDTO.getProductId()));
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }
            // Step c: Decrease stock and save product
            product.setStock(product.getStock() - itemDTO.getQuantity());
            productRepository.save(product);

            // Step d: Create and save OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(itemDTO.getPrice()); // price snapshot
            orderItemRepository.save(orderItem);
            orderItems.add(orderItem);
        }
        // Step e: Link all OrderItems to the Order
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }
    
    @Override
    public DashboardStatsDTO getDashboardStats() {
        return orderRepository.getDashboardStats();
    }
}
