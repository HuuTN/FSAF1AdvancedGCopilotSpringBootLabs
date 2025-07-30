package com.example.demo.services.serviceImpl;

import com.example.demo.cores.entity.Order;
import com.example.demo.cores.repository.OrderRepository;
import com.example.demo.services.service.OrderService;
import com.example.demo.cores.dtos.OrderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.cores.dtos.CreateOrderRequestDTO;
import com.example.demo.cores.dtos.OrderItemDTO;
import com.example.demo.cores.entity.OrderItem;
import java.util.HashSet;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.demo.cores.repository.ProductRepository;
import com.example.demo.cores.repository.OrderItemRepository;
import com.example.demo.cores.entity.Product;
import com.example.demo.exception.InsufficientStockException;
import com.example.demo.utils.CommonUtils;

@Service
public class OrderServiceImpl implements OrderService {
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
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    public Order createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderStatus(orderDTO.getStatus());
        // Set other properties as needed (orderItems, user, etc.)
        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> updateOrder(Long id, OrderDTO orderDTO) {
        return orderRepository.findById(id).map(order -> {
            order.setOrderStatus(orderDTO.getStatus());
            // Set other properties as needed (orderItems, user, etc.)
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
    
    @Override
    @Transactional(rollbackFor = {InsufficientStockException.class, RuntimeException.class})
    public Order placeOrder(CreateOrderRequestDTO createOrderRequestDTO) throws InsufficientStockException {
        // Single responsibility: Orchestrate the order placement process
        validateOrderRequest(createOrderRequestDTO);
        
        Order order = createNewOrder(createOrderRequestDTO);
        
        processOrderItems(createOrderRequestDTO, order);
        
        return persistOrder(order);
    }

    /**
     * Validates the incoming order request
     * Responsibility: Input validation
     */
    private void validateOrderRequest(CreateOrderRequestDTO createOrderRequestDTO) {
        if (CommonUtils.isNull(createOrderRequestDTO)) {
            throw new IllegalArgumentException("Order request cannot be null");
        }
        
        if (createOrderRequestDTO.getOrderItems() == null || createOrderRequestDTO.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
    }

    /**
     * Creates a new Order entity with basic setup
     * Responsibility: Order entity creation and initial setup
     */
    private Order createNewOrder(CreateOrderRequestDTO createOrderRequestDTO) {
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now()); // Add order timestamp
        
        if (createOrderRequestDTO.getOrderStatus() != null) {
            order.setOrderStatus(createOrderRequestDTO.getOrderStatus());
        }
        
        order.setOrderItems(new HashSet<>());
        return order;
    }

    /**
     * Processes all order items in the request
     * Responsibility: Coordinate processing of multiple order items
     */
    private void processOrderItems(CreateOrderRequestDTO createOrderRequestDTO, Order order) 
            throws InsufficientStockException {
        for (OrderItemDTO itemDTO : createOrderRequestDTO.getOrderItems()) {
            if (itemDTO != null) {
                processIndividualOrderItem(itemDTO, order);
            }
        }
    }

    /**
     * Processes a single order item
     * Responsibility: Handle individual order item processing
     */
    private void processIndividualOrderItem(OrderItemDTO itemDTO, Order order) 
            throws InsufficientStockException {
        validateOrderItemDTO(itemDTO);
        
        Product product = getAndValidateProduct(itemDTO.getProductId());
        Integer quantity = itemDTO.getQuantity();
        
        validateAndUpdateStock(product, quantity);
        
        OrderItem orderItem = createOrderItem(order, product, quantity);
        order.getOrderItems().add(orderItem);
    }

    /**
     * Validates individual order item DTO
     * Responsibility: Order item DTO validation
     */
    private void validateOrderItemDTO(OrderItemDTO itemDTO) {
        if (CommonUtils.isEmpty(itemDTO.getProductId().toString())) {
            throw new IllegalArgumentException("Product ID in order item cannot be null");
        }
        
        if (CommonUtils.isEmpty(itemDTO.getQuantity().toString())) {
            throw new IllegalArgumentException("Quantity in order item cannot be null");
        }
        
        if (itemDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    /**
     * Retrieves and validates product exists with pessimistic lock
     * Responsibility: Product retrieval and existence validation with concurrency control
     */
    private Product getAndValidateProduct(Long productId) {
        return productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
    }

    /**
     * Validates stock availability and updates product stock
     * Responsibility: Stock validation and inventory management
     */
    private void validateAndUpdateStock(Product product, Integer quantity) 
            throws InsufficientStockException {
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new InsufficientStockException(
                "Insufficient stock for product: " + product.getName() + 
                ". Available: " + (product.getStock() != null ? product.getStock() : 0) + 
                ", Requested: " + quantity
            );
        }
        
        // Update stock atomically
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    /**
     * Creates and persists an OrderItem entity
     * Responsibility: OrderItem entity creation and persistence
     */
    private OrderItem createOrderItem(Order order, Product product, Integer quantity) {
        validateProductPrice(product);
        
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice()); // Fixed: Use actual product price
        
        return orderItemRepository.save(orderItem);
    }

    /**
     * Validates product price is not null
     * Responsibility: Product price validation
     */
    private void validateProductPrice(Product product) {
        if (CommonUtils.isEmpty(product.getPrice().toString())) {
            throw new IllegalArgumentException(
                "Product price cannot be null for product: " + product.getName()
            );
        }
    }

    /**
     * Persists the completed order
     * Responsibility: Final order persistence
     */
    private Order persistOrder(Order order) {
        return orderRepository.save(order);
    }

}

