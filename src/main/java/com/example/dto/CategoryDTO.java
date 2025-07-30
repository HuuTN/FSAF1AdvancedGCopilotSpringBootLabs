package com.example.dto;

import java.util.Set;

public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private boolean active;
    private Long parentId;
    private Set<Long> childrenIds;
    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Set<Long> getChildrenIds() { return childrenIds; }
    public void setChildrenIds(Set<Long> childrenIds) { this.childrenIds = childrenIds; }
}
