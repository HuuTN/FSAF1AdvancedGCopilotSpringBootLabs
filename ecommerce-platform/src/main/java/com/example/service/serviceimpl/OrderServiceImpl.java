package com.example.service.serviceimpl;

import com.example.model.entity.Order;
import com.example.model.entity.OrderItem;
import com.example.model.entity.Product;
import com.example.model.entity.Customer;
import com.example.model.entity.User;
import com.example.repository.OrderRepository;
import com.example.repository.CustomerRepository;
import com.example.repository.UserRepository;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.model.enums.OrderStatus;
import com.example.exception.ResourceNotFoundException;
import com.example.exception.InsufficientStockException;
import com.example.model.dto.OrderItemDTO;
import com.example.model.dto.OrderRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
            CustomerRepository customerRepository, UserRepository userRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.productService = productService;
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Override
    public List<Order> getOrdersByUserName(String userName) {
        return orderRepository.findByUser_Name(userName);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (!optionalOrder.isPresent()) {
            // Order not found - do nothing and return silently
            return;
        }

        Order order = optionalOrder.get();

        if (OrderStatus.CANCELED.equals(order.getStatus())) {
            // Order is already cancelled - do nothing and return silently
            return;
        }

        if (OrderStatus.COMPLETED.equals(order.getStatus())) {
            // Order is completed - do nothing and return silently
            return;
        }

        // Only process cancellation for PENDING orders
        if (OrderStatus.PENDING.equals(order.getStatus())) {
            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);

            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    if (item.getProduct() != null) {
                        productService.restoreStock(item.getProduct().getId(), item.getQuantity());
                    }
                }
            }
        }
        // For other statuses (SHIPPED, DELIVERED, PROCESSING), do nothing
    }

    @Override
    @Transactional
    public Order placeOrder(OrderRequestDTO orderRequest) {
        // Validate input
        if (orderRequest == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }

        if (orderRequest.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (orderRequest.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        if (orderRequest.getOrderItems() == null || orderRequest.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("Order items cannot be null or empty");
        }

        // Validate order items
        for (OrderItemDTO item : orderRequest.getOrderItems()) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
        }

        // Find user who is placing the order
        User user = userRepository.findById(orderRequest.getUserId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User not found with id: " + orderRequest.getUserId()));

        // Find customer for whom the order is being placed
        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + orderRequest.getCustomerId()));

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(new ArrayList<>());

        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process each order item
        for (OrderItemDTO itemRequest : orderRequest.getOrderItems()) {
            Optional<Product> productOpt = productService.getProductById(itemRequest.getProductId());
            if (!productOpt.isPresent()) {
                throw new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId());
            }

            Product product = productOpt.get();

            // Check stock availability
            if (!productService.hasStock(itemRequest.getProductId(), itemRequest.getQuantity())) {
                throw new InsufficientStockException(product.getName(), product.getStock(), itemRequest.getQuantity());
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());

            order.getItems().add(orderItem);

            // Update product stock
            productService.reduceStock(itemRequest.getProductId(), itemRequest.getQuantity());

            // Calculate total
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }
}
