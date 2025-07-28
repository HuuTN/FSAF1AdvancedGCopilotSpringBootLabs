package com.example.demo.services.serviceImpl;

import com.example.demo.core.dtos.OrderItemsDTO;
import com.example.demo.core.entity.OrderItem;
import com.example.demo.core.entity.Product;
import com.example.demo.core.repository.OrderItemRepository;
import com.example.demo.core.repository.ProductRepository;
import com.example.demo.services.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public OrderItemsDTO createOrderItem(OrderItemsDTO orderItemDTO) {
        Product product = productRepository.findById(orderItemDTO.getProductId()).orElse(null);
        if (product == null) return null;
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setPrice(orderItemDTO.getPrice());
        orderItem = orderItemRepository.save(orderItem);
        return toDTO(orderItem);
    }

    @Override
    public Optional<OrderItemsDTO> getOrderItemById(Long id) {
        return orderItemRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<OrderItemsDTO> getAllOrderItems() {
        return orderItemRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Page<OrderItemsDTO> getAllOrderItems(Pageable pageable) {
        return orderItemRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public OrderItemsDTO updateOrderItem(Long id, OrderItemsDTO orderItemDTO) {
        Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(id);
        if (optionalOrderItem.isPresent()) {
            OrderItem orderItem = optionalOrderItem.get();
            Product product = productRepository.findById(orderItemDTO.getProductId()).orElse(null);
            if (product == null) return null;
            orderItem.setProduct(product);
            orderItem.setQuantity(orderItemDTO.getQuantity());
            orderItem.setPrice(orderItemDTO.getPrice());
            orderItem = orderItemRepository.save(orderItem);
            return toDTO(orderItem);
        }
        return null;
    }

    @Override
    public boolean deleteOrderItem(Long id) {
        if (orderItemRepository.existsById(id)) {
            orderItemRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private OrderItemsDTO toDTO(OrderItem orderItem) {
        return new OrderItemsDTO(
            orderItem.getId(),
            orderItem.getQuantity(),
            orderItem.getPrice(),
            orderItem.getProduct() != null ? orderItem.getProduct().getId() : null
        );
    }
}
