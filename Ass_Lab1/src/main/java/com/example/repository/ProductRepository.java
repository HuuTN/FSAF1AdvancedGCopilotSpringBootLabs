// A Spring Data JPA repository for the Product entity.
package com.example.repository;
import com.example.entity.Product;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.price <= :maxPrice")
    Page<Product> searchProducts(@Param("keyword") String keyword, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM products WHERE category_id = :categoryId", nativeQuery = true)
    int countByCategoryId(@Param("categoryId") Long categoryId);

    @Query(value = "SELECT p.id, p.name, p.price, p.stock, c.id, c.name, SUM(oi.quantity * oi.price) as total_sales " +
            "FROM products p " +
            "JOIN categories c ON p.category_id = c.id " +
            "LEFT JOIN order_items oi ON oi.product_id = p.id " +
            "GROUP BY p.id, p.name, p.price, p.stock, c.id, c.name " +
            "ORDER BY total_sales DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5ProductsByTotalAmount();
}
