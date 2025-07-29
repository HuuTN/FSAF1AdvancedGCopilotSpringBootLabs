package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.model.dto.OrderInfoDTO;
import com.example.model.entity.Order;
import com.example.model.entity.OrderItem;
import com.example.model.entity.Product;
import com.example.model.entity.Customer;
import com.example.model.entity.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Order entity and OrderInfoDTO
 */
@Component
public class OrderMapper {

    /**
     * Convert Order entity to OrderInfoDTO
     * 
     * @param order The order entity to convert
     * @return OrderInfoDTO representation
     */
    public OrderInfoDTO toOrderInfoDTO(Order order) {
        if (order == null) {
            return null;
        }

        Customer customer = order.getCustomer();
        User user = order.getUser();
        List<OrderInfoDTO.ProductInfo> products = order.getItems().stream()
                .map(this::toProductInfo)
                .collect(Collectors.toList());

        return new OrderInfoDTO(
                order.getId(),
                customer != null ? customer.getName() : null,
                customer != null ? customer.getEmail() : null,
                user != null ? user.getName() : null,
                order.getTotalAmount(),
                order.getStatus(),
                products);
    }

    /**
     * Convert OrderItem to ProductInfo
     * 
     * @param item The order item to convert
     * @return ProductInfo representation
     */
    public OrderInfoDTO.ProductInfo toProductInfo(OrderItem item) {
        if (item == null) {
            return null;
        }

        Product product = item.getProduct();
        return new OrderInfoDTO.ProductInfo(
                product.getId(),
                product.getName(),
                product.getCategory() != null ? product.getCategory().getName() : null,
                item.getQuantity(),
                item.getPrice() != null ? item.getPrice().toString() : null);
    }

    /**
     * Convert list of Orders to list of OrderInfoDTOs
     * 
     * @param orders The list of orders to convert
     * @return List of OrderInfoDTO representations
     */
    public List<OrderInfoDTO> toOrderInfoDTOs(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::toOrderInfoDTO)
                .collect(Collectors.toList());
    }
}
