package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.ProductDTO;
import com.example.usermanagement.entity.Category;
import com.example.usermanagement.entity.Product;
import com.example.usermanagement.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    @Autowired
    private ReviewRepository reviewRepository;

    public ProductDTO toDTO(Product product) {
        if (product == null) return null;
        Double avgRating = reviewRepository.findAverageRatingByProduct(product);
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .averageRating(avgRating)
                .build();
    }

    public Product toEntity(ProductDTO dto, Category category) {
        if (dto == null) return null;
        return Product.builder()
                .id(dto.getId())
                .name(dto.getName())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .category(category)
                .build();
    }
} 