package com.example.demo.cores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.cores.entity.Product;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // JPQL query to search for products by a keyword in the name and a maximum price
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.price <= :maxPrice")
    List<Product> searchByNameAndMaxPrice(@Param("keyword") String keyword, @Param("maxPrice") Double maxPrice);

    // Native SQL query to count the number of products for a given category ID - FIXED table name
    @Query(value = "SELECT COUNT(*) FROM products WHERE category_id = :categoryId", nativeQuery = true)
    long countByCategoryId(@Param("categoryId") Long categoryId);
    
    // Find products by category ID using JPQL
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    // Find products by name (case-insensitive partial match)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Find products within a price range
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // Find products with stock greater than specified amount
    @Query("SELECT p FROM Product p WHERE p.stock > :minStock")
    List<Product> findByStockGreaterThan(@Param("minStock") Integer minStock);
    
    // Find products that are out of stock
    @Query("SELECT p FROM Product p WHERE p.stock = 0 OR p.stock IS NULL")
    List<Product> findOutOfStockProducts();
    
    // Find products with average rating above specified value
    @Query("SELECT p FROM Product p WHERE p.averageRating >= :minRating")
    List<Product> findByAverageRatingGreaterThanEqual(@Param("minRating") Double minRating);
    
    // Update product stock - useful for inventory management
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.stock = :stock WHERE p.id = :productId")
    int updateProductStock(@Param("productId") Long productId, @Param("stock") Integer stock);
    
    // Update product average rating - useful for review system
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.averageRating = :averageRating WHERE p.id = :productId")
    int updateProductAverageRating(@Param("productId") Long productId, @Param("averageRating") Double averageRating);
    
    // Find product with pessimistic lock - useful for concurrent stock updates
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdWithLock(@Param("productId") Long productId);
    
    // Find top products by rating
    @Query("SELECT p FROM Product p ORDER BY p.averageRating DESC, p.name ASC")
    List<Product> findTopRatedProducts();
    
    // Find products by multiple criteria (advanced search)
    @Query("SELECT p FROM Product p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:minRating IS NULL OR p.averageRating >= :minRating)")
    List<Product> findByMultipleCriteria(
        @Param("name") String name,
        @Param("minPrice") Double minPrice,
        @Param("maxPrice") Double maxPrice,
        @Param("categoryId") Long categoryId,
        @Param("minRating") Double minRating
    );
}
