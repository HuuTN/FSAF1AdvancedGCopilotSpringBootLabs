package com.example.demo.cores.repository;

import com.example.demo.cores.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
public interface UserRepository extends JpaRepository<User, Long> {
    // Native query to join users and orders, grouping by order id
    @Query(value = "SELECT o.id AS orderId, u.id AS userId, u.name, u.email FROM orders o JOIN users u ON o.user_id = u.id GROUP BY o.id, u.id, u.name, u.email", nativeQuery = true)
    List<Object[]> findOrderUserDetailsGroupByOrderId();
}
