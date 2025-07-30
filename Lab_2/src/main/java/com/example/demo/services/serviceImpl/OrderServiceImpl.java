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
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private OrderItemFactory orderItemFactory;

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
        // Main orchestration - single responsibility: coordinate the order placement process
        Order order = createOrderEntity(request);
        Set<OrderItem> orderItems = processOrderItems(request.getOrderItems(), order);
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }

    /**
     * Creates and initializes the Order entity
     * Responsibility: Order creation and basic setup
     */
    private Order createOrderEntity(com.example.demo.core.dtos.CreateOrderRequestDTO request) {
        Order order = new Order();
        order.setId(request.getOrderId());
        order.setStatus(request.getOrderStatus());
        return order;
    }

    /**
     * Processes all order items in the request
     * Responsibility: Orchestrating order item processing
     */
    private Set<OrderItem> processOrderItems(Set<OrderItemsDTO> orderItemDTOs, Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (OrderItemsDTO itemDTO : orderItemDTOs) {
            OrderItem orderItem = processOrderItem(itemDTO, order);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    /**
     * Processes a single order item
     * Responsibility: Single item processing orchestration
     */
    private OrderItem processOrderItem(OrderItemsDTO itemDTO, Order order) {
        Product product = validateAndRetrieveProduct(itemDTO);
        inventoryService.validateAndUpdateStock(product, itemDTO.getQuantity());
        OrderItem orderItem = orderItemFactory.createOrderItem(order, product, itemDTO);
        return orderItemRepository.save(orderItem);
    }

    /**
     * Validates product existence and retrieves it
     * Responsibility: Product validation and retrieval
     */
    private Product validateAndRetrieveProduct(OrderItemsDTO itemDTO) {
        return productRepository.findById(itemDTO.getProductId())
            .orElseThrow(() -> new InsufficientStockException("Product not found: " + itemDTO.getProductId()));
    }

    @Override
    public DashboardStatsDTO getDashboardStats() {
        return orderRepository.getDashboardStats();
    }
}
