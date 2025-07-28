package com.example.copilot.service;

import com.example.copilot.core.dto.CategoryDTO;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    CategoryDTO create(CategoryDTO dto);
    Optional<CategoryDTO> update(Long id, CategoryDTO dto);
    boolean delete(Long id);
    Optional<CategoryDTO> getById(Long id);
    List<CategoryDTO> getAll();
}
