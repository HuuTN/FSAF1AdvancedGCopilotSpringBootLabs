package com.example.demo.dto;

import com.example.demo.entity.Product;

public class ProductDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Double averageRating;
    private Integer totalReviews;

    public ProductDTO(Long id, String name, Double price, String description, Integer stockQuantity, Double averageRating, Integer totalReviews) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(product.getId(), product.getName()
        , product.getPrice(), product.getDescription(), product.getStockQuantity(), product.getAverageRating(), product.getTotalReviews());
    }

    // toEntity method to convert DTO to Entity
    public Product fromEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setPrice(this.price);
        product.setDescription(this.description);
        return product;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
}