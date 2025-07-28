package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""

            SELECT p FROM Product p
            inner join Category c on p.category.id = c.id
            WHERE
            (:keywords IS NULL
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keywords, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keywords, '%'))
            )
            AND (:categoryId IS NULL OR p.category.id = :categoryId)
            AND (:price IS NULL OR p.price <= :price)
            """)
    Page<Product> search(@Param("keywords") String keywords,
                         @Param("price") java.math.BigDecimal price,
                         @Param("category") Integer categoryId,
                         Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM product WHERE category_id = :categoryId", nativeQuery = true)
    int countByCategory(@Param("categoryId") Long categoryId);
} 