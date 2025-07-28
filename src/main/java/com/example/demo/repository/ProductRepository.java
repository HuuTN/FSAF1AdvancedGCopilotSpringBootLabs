package com.example.demo.repository;

import com.example.demo.entity.Product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
       // Lấy tất cả sản phẩm
    List<Product> findAll();

    // Tìm kiếm theo tên (case-insensitive, chứa chuỗi)
    List<Product> findByNameContainingIgnoreCase(String name);

    // Hoặc custom với query (nếu cần nhiều tiêu chí)
    @Query("SELECT p FROM Product p WHERE "
         + "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) "
         + "AND (:category IS NULL OR p.category.name = :category)")
    Page<Product> searchProducts(@Param("name") String name,
                                @Param("category") String category,
                                Pageable pageable);
}
