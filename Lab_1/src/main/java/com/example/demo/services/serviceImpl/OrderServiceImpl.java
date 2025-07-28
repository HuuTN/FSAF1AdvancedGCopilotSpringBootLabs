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
    @Transactional
    public Order placeOrder(CreateOrderRequestDTO createOrderRequestDTO) throws InsufficientStockException {
        // Use isEmpty for object null check if available, else fallback to requireNonNull
        if (CommonUtils.isNull(createOrderRequestDTO)) {
            throw new IllegalArgumentException("Order request cannot be null");
        }

        // Create and save a new Order entity
        Order order = new Order();
        if (createOrderRequestDTO.getOrderStatus() != null) {
            order.setOrderStatus(createOrderRequestDTO.getOrderStatus());
        }

        order.setOrderItems(new HashSet<>());
        if (createOrderRequestDTO.getOrderItems() != null) {
            for (OrderItemDTO itemDTO : createOrderRequestDTO.getOrderItems()) {
                if (itemDTO == null) {
                    continue; // skip null items
                }
                // Use isEmpty for Long/Integer checks
                if (CommonUtils.isEmpty(itemDTO.getProductId().toString())) {
                    throw new IllegalArgumentException("Product ID in order item cannot be null");
                }
                Long productId = itemDTO.getProductId();
                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
                if (CommonUtils.isEmpty(itemDTO.getQuantity().toString())) {
                    throw new IllegalArgumentException("Quantity in order item cannot be null");
                }
                Integer quantity = itemDTO.getQuantity();
                if (product.getStock() == null || product.getStock() < quantity) {
                    throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
                }
                // 2. Decrease stock and save
                product.setStock(product.getStock() - quantity);
                productRepository.save(product);

                // 3. Create OrderItem with product details and price snapshot
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                // Use isEmpty for Double price check if available, else fallback to requireNonNull
                if (CommonUtils.isEmpty(product.getPrice().toString())) {
                    throw new IllegalArgumentException("Product price cannot be null");
                }
                Double price = product.getPrice();
                orderItem.setPrice(price); // snapshot price

                // 4. Save OrderItem
                orderItem = orderItemRepository.save(orderItem);

                // 5. Link to Order
                order.getOrderItems().add(orderItem);
            }
        }

        // Save and return the order
        return orderRepository.save(order);
    }

}

