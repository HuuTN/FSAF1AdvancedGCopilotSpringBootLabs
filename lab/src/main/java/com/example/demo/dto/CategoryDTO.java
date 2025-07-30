package com.example.demo.dto;

import java.util.Set;

public class CategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Set<Long> childrenIds;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Set<Long> getChildrenIds() { return childrenIds; }
    public void setChildrenIds(Set<Long> childrenIds) { this.childrenIds = childrenIds; }
}
