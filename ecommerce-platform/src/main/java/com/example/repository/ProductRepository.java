// A Spring Data JPA repository for Product entities.
package com.example.repository;

import com.example.model.entity.Product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
        // A JPQL query to search for products by a keyword in the name and a maximum
        // price.
        @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.price <= :maxPrice")
        Page<Product> searchProducts(String keyword, BigDecimal maxPrice, Pageable pageable);

        // Create a JOQL query to find products by keywrord, category ID, minimum price,
        // and maximum price with pagination.
        @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
                        "AND p.price >= :minPrice AND p.price <= :maxPrice")
        Page<Product> findByKeywordAndCategoryAndPriceRange(
                        @Param("keyword") String keyword,
                        @Param("categoryId") Long categoryId,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        Pageable pageable);

        // A native SQL query to count the number of products for a given category ID.
        @Query(value = "SELECT COUNT(*) FROM products WHERE category_id = :categoryId", nativeQuery = true)
        int countByCategoryId(@Param("categoryId") Long categoryId);

        // Native SQL query to list the top 5 products with the highest total sales
        // amount, including all product info and category
        @Query(value = "SELECT p.id, p.name, p.description ,p.price, p.stock, c.id as category_id, c.name as category_name, SUM(oi.quantity * oi.price) AS total_amount "
                        +
                        "FROM order_items oi " +
                        "JOIN products p ON oi.product_id = p.id " +
                        "JOIN categories c ON p.category_id = c.id " +
                        "GROUP BY p.id, p.name, p.description, p.price, p.stock, c.id, c.name " +
                        "ORDER BY total_amount DESC " +
                        "FETCH FIRST 5 ROWS ONLY", nativeQuery = true)
        List<Object[]> findTop5ProductsByTotalAmount();

        // Custom method to restore stock for a product
        @Query(value = "UPDATE products SET stock = stock + :quantity WHERE id = :productId", nativeQuery = true)
        void restoreStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
