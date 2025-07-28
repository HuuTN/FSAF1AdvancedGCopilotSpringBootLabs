package com.example.demo.dto;

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;

public class OrderItemDTO {

    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private double price;

    // convert order item entity to DTO
    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(orderItem.getId());
        orderItemDTO.setOrder(orderItem.getOrder());
        orderItemDTO.setProduct(orderItem.getProduct());
        orderItemDTO.setQuantity(orderItem.getQuantity());
        orderItemDTO.setPrice(orderItem.getPrice());
        return orderItemDTO;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setOrder(Order order) {
        this.orderId = order.getId();
    }
    public void setProduct(Product product) {
        this.productId = product.getId();
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    
}