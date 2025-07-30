package com.example.copilot.core.repository;

import com.example.copilot.core.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Original search method
    @Query("SELECT p FROM Product p WHERE (:keyword IS NULL OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))) AND (:categoryId IS NULL OR p.category.id = :categoryId) AND (:minPrice IS NULL OR p.price >= :minPrice) AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> search(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);
    
    // Optimized query methods using indexes
    
    // Fast name search using idx_product_name index
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Fast category + price range search using idx_product_category_price index
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.price BETWEEN :minPrice AND :maxPrice ORDER BY p.price ASC")
    Page<Product> findByCategoryAndPriceRange(@Param("categoryId") Long categoryId, @Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);
    
    // Fast stock availability check using idx_product_stock index
    @Query("SELECT p FROM Product p WHERE p.stockQuantity >= :minStock ORDER BY p.stockQuantity DESC")
    List<Product> findByStockQuantityGreaterThanEqual(@Param("minStock") Integer minStock);
    
    // Fast top-rated products using idx_product_rating index
    @Query("SELECT p FROM Product p WHERE p.averageRating >= :minRating ORDER BY p.averageRating DESC")
    Page<Product> findTopRatedProducts(@Param("minRating") Double minRating, Pageable pageable);
    
    // Fast category + rating search using idx_product_category_rating index
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.averageRating >= :minRating ORDER BY p.averageRating DESC")
    List<Product> findByCategoryAndMinRating(@Param("categoryId") Long categoryId, @Param("minRating") Double minRating);
    
    // Fast price range + stock check using idx_product_price_stock index
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.stockQuantity > 0 ORDER BY p.price ASC")
    Page<Product> findByPriceRangeInStock(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, Pageable pageable);
    
    // Fast name + category search using idx_product_name_category index
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.category.id = :categoryId")
    List<Product> findByNameAndCategory(@Param("name") String name, @Param("categoryId") Long categoryId);
}
