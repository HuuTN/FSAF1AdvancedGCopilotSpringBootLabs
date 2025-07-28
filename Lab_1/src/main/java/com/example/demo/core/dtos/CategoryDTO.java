package com.example.demo.core.dtos;

import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.HashSet;

public class CategoryDTO {
    @NotNull(message = "Category id cannot be null")
    private Long id;

    @NotNull(message = "Category name cannot be null")
    private String name;

    @NotNull(message = "Child categories cannot be null")
    private Set<CategoryDTO> childrens = new HashSet<>();

    public CategoryDTO() {}

    public CategoryDTO(Long id, String name, Set<CategoryDTO> childrens) {
        this.id = id;
        this.name = name;
        this.childrens = childrens;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CategoryDTO> getChildrens() {
        return childrens;
    }

    public void setChildrens(Set<CategoryDTO> childrens) {
        this.childrens = childrens;
    }
}
