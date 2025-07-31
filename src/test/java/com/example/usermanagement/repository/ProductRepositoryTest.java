package com.example.usermanagement.repository;

import com.example.usermanagement.PostgresTestContainerConfig;
import com.example.usermanagement.entity.Category;
import com.example.usermanagement.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ProductRepositoryTest extends PostgresTestContainerConfig {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().name("TestCat").build();
        category = categoryRepository.save(category);
    }

    @Test
    void saveAndFindById() {
        Product product = Product.builder().name("Prod1").price(BigDecimal.TEN).stock(5).category(category).build();
        product = productRepository.save(product);
        Optional<Product> found = productRepository.findById(product.getId());
        assertTrue(found.isPresent());
        assertEquals("Prod1", found.get().getName());
    }

    @Test
    void findAll() {
        Product product1 = Product.builder().name("Prod1").price(BigDecimal.TEN).stock(5).category(category).build();
        Product product2 = Product.builder().name("Prod2").price(BigDecimal.ONE).stock(2).category(category).build();
        productRepository.save(product1);
        productRepository.save(product2);
        List<Product> products = productRepository.findAll();
        assertTrue(products.size() >= 2);
    }

    @Test
    void countByCategory() {
        Product product = Product.builder().name("Prod1").price(BigDecimal.TEN).stock(5).category(category).build();
        productRepository.save(product);
        int count = productRepository.countByCategory(category.getId());
        assertTrue(count >= 1);
    }

    @Test
    void delete() {
        Product product = Product.builder().name("Prod1").price(BigDecimal.TEN).stock(5).category(category).build();
        product = productRepository.save(product);
        productRepository.deleteById(product.getId());
        assertFalse(productRepository.findById(product.getId()).isPresent());
    }
} 