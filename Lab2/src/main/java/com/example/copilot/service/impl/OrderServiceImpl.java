package com.example.copilot.service.impl;

import com.example.copilot.core.dto.OrderDTO;
import com.example.copilot.core.dto.CreateOrderRequestDTO;
import com.example.copilot.core.dto.OrderItemDTO;
import com.example.copilot.core.dto.CartItem;
import com.example.copilot.core.entity.Order;
import com.example.copilot.core.entity.OrderItem;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.entity.User;
import com.example.copilot.core.enums.OrderStatus;
import com.example.copilot.core.repository.OrderItemRepository;
import com.example.copilot.core.repository.OrderRepository;
import com.example.copilot.core.repository.ProductRepository;
import com.example.copilot.core.repository.UserRepository;
import com.example.copilot.exception.InsufficientStockException;
import com.example.copilot.exception.ResourceNotFoundException;
import com.example.copilot.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
        // Input validation
        validateOrderRequest(request);
        
        // 1. Validation phase - Read-only operations
        User user = validateAndGetUser(request.getUserId());
        
        // 2. Business logic phase - Build order structure
        Order order = buildOrder(user, request.getItems());
        
        // 3. Persistence phase - Database write operations
        Order savedOrder = saveOrderWithItems(order);
        
        return toDTO(savedOrder);
    }

    /**
     * Validates the order request input
     * @param request The order request to validate
     * @throws IllegalArgumentException if request is invalid
     */
    private void validateOrderRequest(CreateOrderRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
    }

    /**
     * Validates user existence and returns the user entity
     * @param userId The user ID to validate
     * @return User entity if found and valid
     * @throws ResourceNotFoundException if user not found
     * @throws IllegalArgumentException if userId is null
     */
    private User validateAndGetUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        verifyUserExists(user);
        return user;
    }

    /**
     * Verifies that the user exists and is valid for placing orders
     * @param user The user entity to verify
     * @throws ResourceNotFoundException if user is null or doesn't exist
     * @throws IllegalArgumentException if user data is invalid
     */
    private void verifyUserExists(User user) {
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        // Additional business rule validations if needed
        if (user.getRole() == null) {
            throw new IllegalArgumentException("User role is required for placing orders");
        }
    }

    /**
     * Builds an Order entity with necessary information before saving to database
     * @param user The user placing the order
     * @param items List of order items to be processed
     * @return Order entity ready to be saved
     */
    private Order buildOrder(User user, List<CreateOrderRequestDTO.OrderItemRequest> items) {
        // Validate inputs
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }
        
        // Create order entity
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);
        
        // Build order items
        Set<OrderItem> orderItems = processOrderItems(items, order);
        order.setOrderItems(orderItems);
        
        return order;
    }

    /**
     * Processes order items: validates products, checks stock, creates order items
     * @param itemRequests List of item requests
     * @param order The parent order
     * @return Set of processed order items
     */
    private Set<OrderItem> processOrderItems(List<CreateOrderRequestDTO.OrderItemRequest> itemRequests, Order order) {
        // Validate all items first (fail-fast approach)
        validateAllOrderItems(itemRequests);
        
        Set<OrderItem> orderItems = new HashSet<>();
        
        for (CreateOrderRequestDTO.OrderItemRequest itemReq : itemRequests) {
            OrderItem orderItem = processIndividualOrderItem(itemReq, order);
            orderItems.add(orderItem);
        }
        
        return orderItems;
    }

    /**
     * Validates all order items before processing (fail-fast approach)
     * @param itemRequests List of item requests to validate
     * @throws IllegalArgumentException if any validation fails
     */
    private void validateAllOrderItems(List<CreateOrderRequestDTO.OrderItemRequest> itemRequests) {
        if (itemRequests == null || itemRequests.isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }
        
        for (CreateOrderRequestDTO.OrderItemRequest itemReq : itemRequests) {
            validateOrderItemRequest(itemReq);
        }
    }

    /**
     * Validates individual order item request
     * @param itemReq The item request to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateOrderItemRequest(CreateOrderRequestDTO.OrderItemRequest itemReq) {
        if (itemReq == null) {
            throw new IllegalArgumentException("Order item request cannot be null");
        }
        if (itemReq.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (itemReq.getQuantity() == null || itemReq.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    /**
     * Processes individual order item: validates product, checks stock, updates inventory, creates order item
     * @param itemReq The item request
     * @param order The parent order
     * @return Created order item
     */
    private OrderItem processIndividualOrderItem(CreateOrderRequestDTO.OrderItemRequest itemReq, Order order) {
        // Step 1: Validate and get product
        Product product = validateAndGetProduct(itemReq.getProductId());
        
        // Step 2: Validate stock availability
        validateStockAvailability(product, itemReq.getQuantity());
        
        // Step 3: Reserve inventory (update stock)
        reserveProductInventory(product, itemReq.getQuantity());
        
        // Step 4: Create order item
        return createOrderItem(order, product, itemReq);
    }

    /**
     * Reserves product inventory by updating stock quantity
     * @param product The product to reserve inventory for
     * @param quantity The quantity to reserve
     */
    private void reserveProductInventory(Product product, Integer quantity) {
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }

    /**
     * Validates product existence and returns the product entity
     * @param productId The product ID to validate
     * @return Product entity if found
     * @throws ResourceNotFoundException if product not found
     */
    private Product validateAndGetProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }

    /**
     * Validates if sufficient stock is available
     * @param product The product to check
     * @param requestedQuantity The requested quantity
     * @throws InsufficientStockException if not enough stock
     */
    private void validateStockAvailability(Product product, Integer requestedQuantity) {
        if (product.getStockQuantity() < requestedQuantity) {
            throw new InsufficientStockException("Not enough stock for product: " + product.getName() + 
                    ". Available: " + product.getStockQuantity() + ", Requested: " + requestedQuantity);
        }
    }

    /**
     * Creates an order item entity
     * @param order The parent order
     * @param product The product for this item
     * @param itemReq The item request with quantity
     * @return Created order item
     */
    private OrderItem createOrderItem(Order order, Product product, CreateOrderRequestDTO.OrderItemRequest itemReq) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(itemReq.getQuantity());
        // FIXED: Use actual product price instead of hardcoded wrong value
        orderItem.setPrice(product.getPrice());
        return orderItem;
    }

    /**
     * Saves order and its items to database
     * @param order The order to save
     * @return Saved order entity
     */
    private Order saveOrderWithItems(Order order) {
        Order savedOrder = orderRepository.save(order);
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            orderItemRepository.saveAll(order.getOrderItems());
        }
        return savedOrder;
    }

    /**
     * Alternative buildOrder method using CartItem for flexibility
     * @param user The user placing the order
     * @param cartItems List of cart items
     * @return Order entity ready to be saved
     */
    private Order buildOrderFromCart(User user, List<CartItem> cartItems) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (cartItems == null || cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart items cannot be null or empty");
        }
        
        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);
        
        Set<OrderItem> orderItems = buildOrderItems(order, cartItems);
        order.setOrderItems(orderItems);
        
        return order;
    }

    /**
     * Builds OrderItem entities from CartItem list
     * @param order The parent order
     * @param cartItems List of cart items
     * @return Set of OrderItem entities
     */
    private Set<OrderItem> buildOrderItems(Order order, List<CartItem> cartItems) {
        Set<OrderItem> orderItems = new HashSet<>();
        
        for (CartItem cartItem : cartItems) {
            Product product = validateAndGetProduct(cartItem.getProductId());
            validateStockAvailability(product, cartItem.getQuantity());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            
            orderItems.add(orderItem);
            reserveProductInventory(product, cartItem.getQuantity());
        }
        
        return orderItems;
    }

    @Override
    public Optional<OrderDTO> getById(Long id) {
        return orderRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<OrderDTO> getAll() {
        return orderRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getByUserId(Long userId) {
        return orderRepository.findAll().stream()
                .filter(o -> o.getUser() != null && o.getUser().getId().equals(userId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(Long id) {
        if (!orderRepository.existsById(id)) return false;
        orderRepository.deleteById(id);
        return true;
    }

    private OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus().name());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        if (order.getOrderItems() != null) {
            Set<OrderItemDTO> items = order.getOrderItems().stream().map(this::toItemDTO).collect(Collectors.toSet());
            dto.setOrderItems(items);
        }
        return dto;
    }
    private OrderItemDTO toItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        return dto;
    }
}
