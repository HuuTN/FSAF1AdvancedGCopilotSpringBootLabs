package com.example.repository;

import com.example.entity.User;
import com.example.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.orders WHERE u.id = :id")
    Optional<User> findByIdWithOrders(@Param("id") Long id);
    
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.reviews WHERE u.id = :id")
    Optional<User> findByIdWithReviews(@Param("id") Long id);
    
    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword% OR u.firstName LIKE %:keyword% OR u.lastName LIKE %:keyword%")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(o) FROM User u JOIN u.orders o WHERE u.id = :userId")
    Long countOrders(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM User u JOIN u.reviews r WHERE u.id = :userId")
    Long countReviews(@Param("userId") Long userId);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.id IN (SELECT DISTINCT o.user.id FROM Order o WHERE o.totalAmount > :amount)")
    List<User> findUsersWithOrdersAboveAmount(@Param("amount") Double amount);
}
