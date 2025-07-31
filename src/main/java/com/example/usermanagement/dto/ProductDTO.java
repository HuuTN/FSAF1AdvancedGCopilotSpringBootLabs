package com.example.usermanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Name must not be blank")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be >= 0")
    private BigDecimal price;

    @Min(value = 0, message = "Stock must be >= 0")
    private int stock;

    @NotNull(message = "Category is required")
    private Long categoryId;
    private String categoryName;

    // Trường mới: rating trung bình
    private Double averageRating;
} 