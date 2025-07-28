package com.example.demo.cores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.cores.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JPQL query to search for products by a keyword in the name and a maximum price
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.price <= :maxPrice")
    List<Product> searchByNameAndMaxPrice(@Param("keyword") String keyword, @Param("maxPrice") Double maxPrice);

    // Native SQL query to count the number of products for a given category ID
    @Query(value = "SELECT COUNT(*) FROM product WHERE category_id = :categoryId", nativeQuery = true)
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
