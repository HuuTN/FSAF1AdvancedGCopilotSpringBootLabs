//Annotate it with @DataJpaTest.
//Add @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) to tell Spring to use your Testcontainers configuration instead of an in-memory database.
// Integration test for the custom searchProducts method in ProductRepository.
//generate a test method. Inside, first save some test Product entities.
//Call the productRepository.searchProducts(...) method and assert that the returned Page contains the correct results
package com.example.repository;
import com.example.entity.Product;
import com.example.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import static org.junit.jupiter.api.Assertions.assertEquals;
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties") // Use Testcontainers configuration
public class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void whenSearchProducts_thenReturnMatchingProducts() {
        // Create and save test categories
        Category electronics = new Category();
        electronics.setName("Electronics");
        Category clothing = new Category();
        clothing.setName("Clothing");
        categoryRepository.save(electronics);
        categoryRepository.save(clothing);

        // Create and save test products
        Product product1 = new Product();
        product1.setName("Smartphone");
        product1.setPrice(BigDecimal.valueOf(699.99));
        product1.setStock(50);
        product1.setCategory(electronics);

        Product product2 = new Product();
        product2.setName("Laptop");
        product2.setPrice(BigDecimal.valueOf(999.99));
        product2.setStock(30);
        product2.setCategory(electronics);

        Product product3 = new Product();
        product3.setName("T-Shirt");
        product3.setPrice(BigDecimal.valueOf(19.99));
        product3.setStock(100);
        product3.setCategory(clothing);

        productRepository.saveAll(List.of(product1, product2, product3));

        // Search for products with keyword "smart" and max price of 1000
        Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Product> resultPage = productRepository.searchProducts("smart", BigDecimal.valueOf(1000), pageable);

        // Assert that the result contains the expected products
        List<Product> products = StreamSupport.stream(resultPage.spliterator(), false).collect(Collectors.toList());
        assertEquals(1, products.size());
        assertEquals("Smartphone", products.get(0).getName());
    }
}
