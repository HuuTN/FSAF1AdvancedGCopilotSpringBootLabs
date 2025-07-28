package com.example.controller;

import com.example.model.dto.CategoryDTO;
import com.example.model.entity.Category;
import com.example.service.CategoryService;
import com.example.mapper.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

        @Autowired
        private CategoryService categoryService;

        @Autowired
        private CategoryMapper categoryMapper;

        // CRUD Operations

        @Operation(summary = "Create a new category", description = "Create a new category in the system.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Category created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @PostMapping(consumes = "application/json", produces = "application/json")
        public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
                Category category = categoryMapper.toEntity(categoryDTO);
                Category createdCategory = categoryService.createCategory(category);
                CategoryDTO dto = categoryMapper.toDTO(createdCategory);
                return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        }

        @Operation(summary = "Get all categories", description = "Retrieve all categories with pagination.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
        })
        @GetMapping
        public ResponseEntity<Page<CategoryDTO>> getAllCategories(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {
                Page<Category> categories = categoryService.getAllCategories(PageRequest.of(page, size));
                Page<CategoryDTO> dtos = categories.map(categoryMapper::toDTO);
                return ResponseEntity.ok(dtos);
        }

        @Operation(summary = "Get all categories as list", description = "Retrieve all categories without pagination.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
        })
        @GetMapping("/all")
        public ResponseEntity<List<CategoryDTO>> getAllCategoriesAsList() {
                List<Category> categories = categoryService.getAllCategories();
                List<CategoryDTO> dtos = categories.stream()
                                .map(categoryMapper::toDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @Operation(summary = "Get category by ID", description = "Retrieve a category by its ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category found and returned successfully"),
                        @ApiResponse(responseCode = "404", description = "Category not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
                Optional<Category> category = categoryService.getCategoryById(id);
                if (category.isPresent()) {
                        CategoryDTO dto = categoryMapper.toDTO(category.get());
                        return ResponseEntity.ok(dto);
                }
                return ResponseEntity.notFound().build();
        }

        @Operation(summary = "Update a category", description = "Update an existing category by ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Category not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid input data")
        })
        @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
        public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                        @Valid @RequestBody CategoryDTO categoryDTO) {
                Category categoryDetails = categoryMapper.toEntity(categoryDTO);
                Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
                CategoryDTO dto = categoryMapper.toDTO(updatedCategory);
                return ResponseEntity.ok(dto);
        }

        @Operation(summary = "Delete a category", description = "Delete a category by ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Category not found"),
                        @ApiResponse(responseCode = "400", description = "Cannot delete category with subcategories")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
                categoryService.deleteCategory(id);
                return ResponseEntity.noContent().build();
        }

        // Hierarchical Operations

        @Operation(summary = "Get root categories", description = "Get all root categories (categories without parent).")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Root categories retrieved successfully")
        })
        @GetMapping("/root")
        public ResponseEntity<List<CategoryDTO>> getRootCategories() {
                List<Category> rootCategories = categoryService.getRootCategories();
                List<CategoryDTO> dtos = rootCategories.stream()
                                .map(categoryMapper::toDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @Operation(summary = "Get subcategories", description = "Get all subcategories of a parent category.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Subcategories retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Parent category not found")
        })
        @GetMapping("/{parentId}/subcategories")
        public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable Long parentId) {
                List<Category> subcategories = categoryService.getSubcategories(parentId);
                List<CategoryDTO> dtos = subcategories.stream()
                                .map(categoryMapper::toDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(dtos);
        }

        @Operation(summary = "Check if category exists", description = "Check if a category exists by ID.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Category existence check completed")
        })
        @GetMapping("/{id}/exists")
        public ResponseEntity<Boolean> categoryExists(@PathVariable Long id) {
                boolean exists = categoryService.categoryExists(id);
                return ResponseEntity.ok(exists);
        }
}
