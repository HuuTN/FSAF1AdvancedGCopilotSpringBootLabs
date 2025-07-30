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
        // 1. Validate input
        validateOrderRequest(orderRequest);

        // 2. Retrieve entities
        User user = findUserById(orderRequest.getUserId());
        Customer customer = findCustomerById(orderRequest.getCustomerId());

        // 3. Create order structure
        Order order = createOrderEntity(user, customer);

        // 4. Process order items
        BigDecimal totalAmount = processOrderItems(order, orderRequest.getOrderItems());

        // 5. Finalize and save
        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    private void validateOrderRequest(OrderRequestDTO orderRequest) {
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

        validateOrderItems(orderRequest.getOrderItems());
    }

    private void validateOrderItems(List<OrderItemDTO> orderItems) {
        for (OrderItemDTO item : orderItems) {
            if (item.getProductId() == null) {
                throw new IllegalArgumentException("Product ID cannot be null");
            }
            if (item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
    }

    private Order createOrderEntity(User user, Customer customer) {
        Order order = new Order();
        order.setUser(user);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(new ArrayList<>());
        return order;
    }

    private BigDecimal processOrderItems(Order order, List<OrderItemDTO> orderItemDTOs) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO itemRequest : orderItemDTOs) {
            Product product = validateAndGetProduct(itemRequest);
            validateStockAvailability(product, itemRequest.getQuantity());

            OrderItem orderItem = createOrderItem(order, product, itemRequest);
            order.getItems().add(orderItem);

            updateProductStock(itemRequest.getProductId(), itemRequest.getQuantity());
            totalAmount = calculateItemTotal(totalAmount, product.getPrice(), itemRequest.getQuantity());
        }

        return totalAmount;
    }

    private Product validateAndGetProduct(OrderItemDTO itemRequest) {
        Optional<Product> productOpt = productService.getProductById(itemRequest.getProductId());
        if (!productOpt.isPresent()) {
            throw new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId());
        }
        return productOpt.get();
    }

    private void validateStockAvailability(Product product, int requestedQuantity) {
        if (!productService.hasStock(product.getId(), requestedQuantity)) {
            throw new InsufficientStockException(product.getName(), product.getStock(), requestedQuantity);
        }
    }

    private OrderItem createOrderItem(Order order, Product product, OrderItemDTO itemRequest) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setPrice(product.getPrice());
        return orderItem;
    }

    private void updateProductStock(Long productId, int quantity) {
        productService.reduceStock(productId, quantity);
    }

    private BigDecimal calculateItemTotal(BigDecimal currentTotal, BigDecimal unitPrice, int quantity) {
        return currentTotal.add(unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }
}
