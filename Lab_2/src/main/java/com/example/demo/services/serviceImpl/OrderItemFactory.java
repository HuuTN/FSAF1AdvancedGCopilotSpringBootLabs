package com.example.demo.services.serviceImpl;

import com.example.demo.core.dtos.OrderItemsDTO;
import com.example.demo.core.entity.Order;
import com.example.demo.core.entity.OrderItem;
import com.example.demo.core.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class OrderItemFactory {
    public OrderItem createOrderItem(Order order, Product product, OrderItemsDTO itemDTO) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(itemDTO.getQuantity());
        orderItem.setPrice(itemDTO.getPrice());
        return orderItem;
    }
}
