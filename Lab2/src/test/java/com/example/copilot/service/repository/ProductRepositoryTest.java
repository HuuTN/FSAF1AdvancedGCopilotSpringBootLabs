package com.example.copilot.service.repository;

import com.example.copilot.core.entity.Category;
import com.example.copilot.core.entity.Product;
import com.example.copilot.core.repository.CategoryRepository;
import com.example.copilot.core.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {
    @Autowired private ProductRepository productRepository;
    @Autowired private CategoryRepository categoryRepository;

    @Test
    void whenSearchProducts_thenReturnMatchingProducts() {
        // Arrange
        Category category = new Category();
        category.setName("Electronics");
        categoryRepository.save(category);
        Product laptop = new Product();
        laptop.setName("Gaming Laptop");
        laptop.setPrice(1499.99);
        laptop.setCategory(category);
        laptop.setStockQuantity(10); // Bổ sung trường bắt buộc
        productRepository.save(laptop);
        // Act
        Page<Product> results = productRepository.search("Laptop", null, null, null, PageRequest.of(0, 5));
        // Assert
        assertThat(results.getContent()).isNotEmpty();
    }
}
