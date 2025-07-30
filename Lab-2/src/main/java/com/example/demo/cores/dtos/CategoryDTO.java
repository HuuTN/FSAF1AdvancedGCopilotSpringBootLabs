package com.example.demo.cores.dtos;

import java.util.Set;

public class CategoryDTO {
    private String name;
    private Long parentId;
    private Set<Long> childrenIds;

    public CategoryDTO() {}

    public CategoryDTO(String name, Long parentId, Set<Long> childrenIds) {
        this.name = name;
        this.parentId = parentId;
        this.childrenIds = childrenIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Set<Long> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(Set<Long> childrenIds) {
        this.childrenIds = childrenIds;
    }
}
