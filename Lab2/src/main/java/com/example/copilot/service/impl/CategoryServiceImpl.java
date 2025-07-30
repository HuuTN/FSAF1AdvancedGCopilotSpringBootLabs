package com.example.copilot.service.impl;

import com.example.copilot.core.dto.CategoryDTO;
import com.example.copilot.core.entity.Category;
import com.example.copilot.core.repository.CategoryRepository;
import com.example.copilot.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        if (category.getChildren() != null) {
            Set<Long> childrenIds = category.getChildren().stream().map(Category::getId).collect(Collectors.toSet());
            dto.setChildrenIds(childrenIds);
        }
        return dto;
    }

    private Category toEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        // parent và children sẽ được xử lý ở controller/service nếu cần
        return category;
    }

    @Override
    public CategoryDTO create(CategoryDTO dto) {
        Category category = toEntity(dto);
        Category saved = categoryRepository.save(category);
        return toDTO(saved);
    }

    @Override
    public Optional<CategoryDTO> update(Long id, CategoryDTO dto) {
        if (!categoryRepository.existsById(id)) return Optional.empty();
        Category category = toEntity(dto);
        category.setId(id);
        Category saved = categoryRepository.save(category);
        return Optional.of(toDTO(saved));
    }

    @Override
    public boolean delete(Long id) {
        if (!categoryRepository.existsById(id)) return false;
        categoryRepository.deleteById(id);
        return true;
    }

    @Override
    public Optional<CategoryDTO> getById(Long id) {
        return categoryRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
}
