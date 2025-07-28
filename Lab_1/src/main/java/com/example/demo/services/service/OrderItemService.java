package com.example.demo.services.service;

import com.example.demo.core.dtos.OrderItemsDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {
    OrderItemsDTO createOrderItem(OrderItemsDTO orderItemDTO);
    Optional<OrderItemsDTO> getOrderItemById(Long id);
    List<OrderItemsDTO> getAllOrderItems();
    OrderItemsDTO updateOrderItem(Long id, OrderItemsDTO orderItemDTO);
    boolean deleteOrderItem(Long id);
    Page<OrderItemsDTO> getAllOrderItems(Pageable pageable);
}
