package com.example.demo.core.repository;

import com.example.demo.core.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Custom query methods for OrderItem can be added here
}
