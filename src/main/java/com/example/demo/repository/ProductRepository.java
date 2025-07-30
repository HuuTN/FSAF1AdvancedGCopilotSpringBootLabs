package com.example.demo.repository;

import com.example.demo.entity.Product;

import jakarta.persistence.QueryHint;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
       // Lấy tất cả sản phẩm
    List<Product> findAll();

    // Tìm kiếm theo tên (case-insensitive, chứa chuỗi)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Hoặc custom với query (nếu cần nhiều tiêu chí)
    @Query(value = "SELECT p FROM Product p WHERE "
         + "(:name IS NULL OR p.name LIKE CONCAT('%', UPPER(:name), '%')) "
         + "AND (:category IS NULL OR p.category.name = :category)")
    @QueryHints({@QueryHint(name = "org.hibernate.cacheable", value = "true")})
    Page<Product> searchProducts(@Param("name") String name,
                                @Param("category") String category,
                                Pageable pageable);
}
