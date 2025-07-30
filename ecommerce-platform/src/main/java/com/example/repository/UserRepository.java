// JPA Repository interface for User entity
package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Write a native query to find users by their email using LIKE for partial matching
    @Query(value = "SELECT * FROM users WHERE email LIKE %?1%", nativeQuery = true)
    List<User> findByEmail(String email);
}
