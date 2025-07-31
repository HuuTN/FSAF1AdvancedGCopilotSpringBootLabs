package com.example.copilot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.example.copilot.entity.base.Auditable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
    // Index for case-insensitive name searches (most critical for LIKE queries)
    @Index(name = "idx_product_name_lower", columnList = "name"),
    
    // Composite index for common filter combinations
    @Index(name = "idx_product_category_price", columnList = "category_id, price"),
    
    // Index for price range queries
    @Index(name = "idx_product_price", columnList = "price"),
    
    // Index for stock-based queries
    @Index(name = "idx_product_stock", columnList = "stock"),
    
    // Index for category-based filtering
    @Index(name = "idx_product_category", columnList = "category_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Product extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Product name is required")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @Column(nullable = false)
    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    @Column(name = "review_count")
    private Integer reviewCount = 0;
}
