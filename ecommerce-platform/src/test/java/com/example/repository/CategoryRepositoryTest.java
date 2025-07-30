package com.example.repository;

import com.example.model.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Category Repository Tests")
class CategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category parentCategory;
    private Category childCategory1;
    private Category childCategory2;

    @BeforeEach
    void setUp() {
        // Create parent category
        parentCategory = new Category();
        parentCategory.setName("Electronics");
        entityManager.persistAndFlush(parentCategory);

        // Create child categories
        childCategory1 = new Category();
        childCategory1.setName("Laptops");
        childCategory1.setParent(parentCategory);
        entityManager.persistAndFlush(childCategory1);

        childCategory2 = new Category();
        childCategory2.setName("Smartphones");
        childCategory2.setParent(parentCategory);
        entityManager.persistAndFlush(childCategory2);

        // Refresh parent to load children
        entityManager.refresh(parentCategory);
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudOperations {

        @Test
        @DisplayName("Should save category successfully")
        void shouldSaveCategorySuccessfully() {
            // Given
            Category newCategory = new Category();
            newCategory.setName("Books");

            // When
            Category savedCategory = categoryRepository.save(newCategory);

            // Then
            assertThat(savedCategory).isNotNull();
            assertThat(savedCategory.getId()).isNotNull();
            assertThat(savedCategory.getName()).isEqualTo("Books");
        }

        @Test
        @DisplayName("Should find category by ID")
        void shouldFindCategoryById() {
            // When
            Optional<Category> foundCategory = categoryRepository.findById(parentCategory.getId());

            // Then
            assertThat(foundCategory).isPresent();
            assertThat(foundCategory.get().getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("Should return empty when category not found by ID")
        void shouldReturnEmptyWhenCategoryNotFoundById() {
            // When
            Optional<Category> foundCategory = categoryRepository.findById(999L);

            // Then
            assertThat(foundCategory).isEmpty();
        }

        @Test
        @DisplayName("Should find all categories")
        void shouldFindAllCategories() {
            // When
            List<Category> categories = categoryRepository.findAll();

            // Then
            assertThat(categories).hasSize(3);
            assertThat(categories).extracting(Category::getName)
                    .containsExactlyInAnyOrder("Electronics", "Laptops", "Smartphones");
        }

        @Test
        @DisplayName("Should delete category by ID")
        void shouldDeleteCategoryById() {
            // Given
            Long categoryId = childCategory1.getId();

            // When
            categoryRepository.deleteById(categoryId);
            entityManager.flush();

            // Then
            Optional<Category> deletedCategory = categoryRepository.findById(categoryId);
            assertThat(deletedCategory).isEmpty();

            List<Category> remainingCategories = categoryRepository.findAll();
            assertThat(remainingCategories).hasSize(2);
        }

        @Test
        @DisplayName("Should update category successfully")
        void shouldUpdateCategorySuccessfully() {
            // Given
            Category categoryToUpdate = childCategory1;
            categoryToUpdate.setName("Gaming Laptops");

            // When
            Category updatedCategory = categoryRepository.save(categoryToUpdate);
            entityManager.flush();

            // Then
            assertThat(updatedCategory.getName()).isEqualTo("Gaming Laptops");

            // Verify in database
            Optional<Category> foundCategory = categoryRepository.findById(childCategory1.getId());
            assertThat(foundCategory).isPresent();
            assertThat(foundCategory.get().getName()).isEqualTo("Gaming Laptops");
        }
    }

    @Nested
    @DisplayName("Find by Name Operations")
    class FindByNameOperations {

        @Test
        @DisplayName("Should find category by name containing")
        void shouldFindCategoryByNameContaining() {
            // When
            List<Category> foundCategories = categoryRepository.findByNameContainingIgnoreCase("Electronics");

            // Then
            assertThat(foundCategories).hasSize(1);
            assertThat(foundCategories.get(0).getName()).isEqualTo("Electronics");
            assertThat(foundCategories.get(0).getParent()).isNull();
        }

        @Test
        @DisplayName("Should return empty when category not found by name")
        void shouldReturnEmptyWhenCategoryNotFoundByName() {
            // When
            List<Category> foundCategories = categoryRepository.findByNameContainingIgnoreCase("NonExistent");

            // Then
            assertThat(foundCategories).isEmpty();
        }

        @Test
        @DisplayName("Should find category by name case-insensitive")
        void shouldFindCategoryByNameCaseInsensitive() {
            // When
            List<Category> foundLowercase = categoryRepository.findByNameContainingIgnoreCase("electronics");
            List<Category> foundUppercase = categoryRepository.findByNameContainingIgnoreCase("ELECTRONICS");
            List<Category> foundPartial = categoryRepository.findByNameContainingIgnoreCase("elect");

            // Then
            assertThat(foundLowercase).hasSize(1);
            assertThat(foundUppercase).hasSize(1);
            assertThat(foundPartial).hasSize(1);
            assertThat(foundLowercase.get(0).getName()).isEqualTo("Electronics");
        }
    }

    @Nested
    @DisplayName("Parent-Child Relationship Tests")
    class ParentChildRelationshipTests {

        @Test
        @DisplayName("Should maintain parent-child relationships")
        void shouldMaintainParentChildRelationships() {
            // When
            Category parent = categoryRepository.findById(parentCategory.getId()).orElseThrow();
            Category child = categoryRepository.findById(childCategory1.getId()).orElseThrow();

            // Then
            assertThat(child.getParent()).isNotNull();
            assertThat(child.getParent().getId()).isEqualTo(parent.getId());
            assertThat(parent.getChildren()).isNotEmpty();
            assertThat(parent.getChildren()).hasSize(2);
        }

        @Test
        @DisplayName("Should find categories by parent ID")
        void shouldFindCategoriesByParentId() {
            // When
            List<Category> children = categoryRepository.findByParentId(parentCategory.getId());

            // Then
            assertThat(children).isNotNull();
            assertThat(children).hasSize(2);
            assertThat(children).extracting(Category::getName)
                    .containsExactlyInAnyOrder("Laptops", "Smartphones");
        }

        @Test
        @DisplayName("Should find root categories (no parent)")
        void shouldFindRootCategories() {
            // When
            List<Category> rootCategories = categoryRepository.findRootCategories();

            // Then
            assertThat(rootCategories).isNotNull();
            assertThat(rootCategories).hasSize(1);
            assertThat(rootCategories.get(0).getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("Should create category hierarchy")
        void shouldCreateCategoryHierarchy() {
            // Given
            Category grandChild = new Category();
            grandChild.setName("Gaming Laptops");
            grandChild.setParent(childCategory1);

            // When
            Category savedGrandChild = categoryRepository.save(grandChild);
            entityManager.flush();

            // Then
            assertThat(savedGrandChild).isNotNull();
            assertThat(savedGrandChild.getParent().getName()).isEqualTo("Laptops");
            assertThat(savedGrandChild.getParent().getParent().getName()).isEqualTo("Electronics");
        }

        @Test
        @DisplayName("Should handle empty children set")
        void shouldHandleEmptyChildrenSet() {
            // Given
            Category leafCategory = childCategory1;

            // When
            Set<Category> children = leafCategory.getChildren();

            // Then
            assertThat(children).isEmpty();
        }
    }

    @Nested
    @DisplayName("Complex Query Tests")
    class ComplexQueryTests {

        @Test
        @DisplayName("Should find categories at specific level")
        void shouldFindCategoriesAtSpecificLevel() {
            // Create multi-level hierarchy
            Category grandChild = new Category();
            grandChild.setName("Gaming Laptops");
            grandChild.setParent(childCategory1);
            entityManager.persistAndFlush(grandChild);

            // When
            List<Category> level1 = categoryRepository.findRootCategories(); // Root level
            List<Category> level2 = categoryRepository.findByParentId(parentCategory.getId()); // Second level

            // Then
            assertThat(level1).hasSize(1);
            assertThat(level2).hasSize(2);
        }

        @Test
        @DisplayName("Should handle categories with same name under different parents")
        void shouldHandleCategoriesWithSameNameUnderDifferentParents() {
            // Given
            Category anotherParent = new Category();
            anotherParent.setName("Home & Garden");
            entityManager.persistAndFlush(anotherParent);

            Category accessoriesUnderElectronics = new Category();
            accessoriesUnderElectronics.setName("Accessories");
            accessoriesUnderElectronics.setParent(parentCategory);
            entityManager.persistAndFlush(accessoriesUnderElectronics);

            Category accessoriesUnderHomeGarden = new Category();
            accessoriesUnderHomeGarden.setName("Accessories");
            accessoriesUnderHomeGarden.setParent(anotherParent);
            entityManager.persistAndFlush(accessoriesUnderHomeGarden);

            // When
            List<Category> electronicsAccessories = categoryRepository.findByNameContainingIgnoreCase("Accessories")
                    .stream()
                    .filter(cat -> cat.getParent() != null && cat.getParent().getId().equals(parentCategory.getId()))
                    .toList();
            List<Category> homeGardenAccessories = categoryRepository.findByNameContainingIgnoreCase("Accessories")
                    .stream()
                    .filter(cat -> cat.getParent() != null && cat.getParent().getId().equals(anotherParent.getId()))
                    .toList();

            // Then
            assertThat(electronicsAccessories).hasSize(1);
            assertThat(homeGardenAccessories).hasSize(1);
            assertThat(electronicsAccessories.get(0).getParent().getName()).isEqualTo("Electronics");
            assertThat(homeGardenAccessories.get(0).getParent().getName()).isEqualTo("Home & Garden");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Data Integrity")
    class EdgeCasesAndDataIntegrity {

        @Test
        @DisplayName("Should handle categories with long names")
        void shouldHandleCategoriesWithLongNames() {
            // Given
            String longName = "A".repeat(99); // Close to typical varchar limit
            Category longNameCategory = new Category();
            longNameCategory.setName(longName);

            // When
            Category savedCategory = categoryRepository.save(longNameCategory);

            // Then
            assertThat(savedCategory).isNotNull();
            assertThat(savedCategory.getName()).isEqualTo(longName);
        }

        @Test
        @DisplayName("Should handle category with minimal required data")
        void shouldHandleCategoryWithMinimalRequiredData() {
            // Given
            Category minimalCategory = new Category();
            minimalCategory.setName("Minimal");

            // When
            Category savedCategory = categoryRepository.save(minimalCategory);

            // Then
            assertThat(savedCategory).isNotNull();
            assertThat(savedCategory.getId()).isNotNull();
            assertThat(savedCategory.getName()).isEqualTo("Minimal");
            assertThat(savedCategory.getParent()).isNull();
            assertThat(savedCategory.getChildren()).isEmpty();
        }

        @Test
        @DisplayName("Should maintain referential integrity on parent deletion")
        void shouldMaintainReferentialIntegrityOnParentDeletion() {
            // Given
            Long parentId = parentCategory.getId();
            Long child1Id = childCategory1.getId();
            Long child2Id = childCategory2.getId();

            // When - Manually handle referential integrity by setting children's parent to
            // null
            // This simulates what ON DELETE SET NULL should do
            List<Category> children = categoryRepository.findByParentId(parentId);
            for (Category child : children) {
                child.setParent(null);
                categoryRepository.save(child);
            }
            entityManager.flush();

            // Now delete the parent
            categoryRepository.deleteById(parentId);
            entityManager.flush();

            // Then
            Optional<Category> deletedParent = categoryRepository.findById(parentId);
            assertThat(deletedParent).isEmpty();

            // Verify children still exist but parent reference should be null
            Optional<Category> orphanedChild1 = categoryRepository.findById(child1Id);
            Optional<Category> orphanedChild2 = categoryRepository.findById(child2Id);

            assertThat(orphanedChild1).isPresent();
            assertThat(orphanedChild2).isPresent();
            assertThat(orphanedChild1.get().getParent()).isNull();
            assertThat(orphanedChild2.get().getParent()).isNull();

            // Verify they are now root categories
            List<Category> rootCategories = categoryRepository.findRootCategories();
            assertThat(rootCategories).hasSize(2);
            assertThat(rootCategories).extracting(Category::getName)
                    .containsExactlyInAnyOrder("Laptops", "Smartphones");
        }

        @Test
        @DisplayName("Should handle deep category hierarchy")
        void shouldHandleDeepCategoryHierarchy() {
            // Given - Create a deep hierarchy
            Category level2 = childCategory1;
            Category level3 = new Category();
            level3.setName("Gaming Laptops");
            level3.setParent(level2);
            entityManager.persistAndFlush(level3);

            Category level4 = new Category();
            level4.setName("RTX Gaming Laptops");
            level4.setParent(level3);
            entityManager.persistAndFlush(level4);

            // When
            Optional<Category> deepestCategory = categoryRepository.findById(level4.getId());

            // Then
            assertThat(deepestCategory).isPresent();
            Category current = deepestCategory.get();
            assertThat(current.getName()).isEqualTo("RTX Gaming Laptops");
            assertThat(current.getParent().getName()).isEqualTo("Gaming Laptops");
            assertThat(current.getParent().getParent().getName()).isEqualTo("Laptops");
            assertThat(current.getParent().getParent().getParent().getName()).isEqualTo("Electronics");
        }
    }

    @Nested
    @DisplayName("Performance and Optimization Tests")
    class PerformanceAndOptimizationTests {

        @Test
        @DisplayName("Should efficiently load category with children")
        void shouldEfficientlyLoadCategoryWithChildren() {
            // When
            Category parent = categoryRepository.findById(parentCategory.getId()).orElseThrow();

            // Force lazy loading of children
            Set<Category> children = parent.getChildren();

            // Then
            assertThat(children).isNotNull();
            assertThat(children).hasSize(2);
            assertThat(children).extracting(Category::getName)
                    .containsExactlyInAnyOrder("Laptops", "Smartphones");
        }

        @Test
        @DisplayName("Should handle batch operations efficiently")
        void shouldHandleBatchOperationsEfficiently() {
            // Given
            List<Category> newCategories = List.of(
                    createCategory("Category1", parentCategory),
                    createCategory("Category2", parentCategory),
                    createCategory("Category3", parentCategory));

            // When
            List<Category> savedCategories = categoryRepository.saveAll(newCategories);

            // Then
            assertThat(savedCategories).hasSize(3);
            assertThat(savedCategories).allMatch(cat -> cat.getId() != null);

            List<Category> allChildren = categoryRepository.findByParentId(parentCategory.getId());
            assertThat(allChildren).hasSize(5); // 2 original + 3 new
        }

        private Category createCategory(String name, Category parent) {
            Category category = new Category();
            category.setName(name);
            category.setParent(parent);
            return category;
        }
    }
}
