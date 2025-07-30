package com.example.repository;

import com.example.entity.Category;
import com.example.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends BaseRepository<Category, Long> {
    
    Optional<Category> findByNameIgnoreCase(String name);
    
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findAllRootCategories();
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId")
    List<Category> findByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.active = true")
    Page<Category> findAllActive(Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Category c JOIN c.products p WHERE c.id = :categoryId")
    Long countProducts(@Param("categoryId") Long categoryId);
    
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.children WHERE c.id = :id")
    Optional<Category> findByIdWithChildren(@Param("id") Long id);
    
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);
    
    @Query("SELECT c FROM Category c WHERE SIZE(c.children) = 0")
    List<Category> findAllLeafCategories();
}
