package com.example.demo.dto;

import com.example.demo.entity.Category;

public class CategoryDTO {

    private Long id;
    private String name;

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryDTO fromEntity(Category category) {
        return new CategoryDTO(category.getId(), category.getName());
    }

    // toEntity method to convert DTO to Entity
    public Category fromEntity() {
        Category category = new Category();
        category.setId(this.id);
        category.setName(this.name);
        return category;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
}