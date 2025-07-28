package com.example.repository;

import com.example.model.entity.User;
import com.example.model.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        userRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        // Setup test users
        testUser1 = User.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(UserRole.EMPLOYEE)
                .build();

        testUser2 = User.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .password("password456")
                .role(UserRole.ADMIN)
                .build();

        testUser3 = User.builder()
                .name("Bob Johnson")
                .email("bob.johnson@example.com")
                .password("password789")
                .role(UserRole.MANAGER)
                .build();
    }

    @Test
    void save_ShouldPersistUser() {
        // When
        User savedUser = userRepository.save(testUser1);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedUser.getPassword()).isEqualTo("password123");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.EMPLOYEE);
    }

    @Test
    void findById_WithExistingId_ShouldReturnUser() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    void findById_WithNonExistingId_ShouldReturnEmpty() {
        // When
        Optional<User> foundUser = userRepository.findById(999L);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(testUser3);

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith", "Bob Johnson");
    }

    @Test
    void findAll_WithPageable_ShouldReturnPagedResults() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(testUser3);

        Pageable pageable = PageRequest.of(0, 2);

        // When
        Page<User> userPage = userRepository.findAll(pageable);

        // Then
        assertThat(userPage.getContent()).hasSize(2);
        assertThat(userPage.getTotalElements()).isEqualTo(3);
        assertThat(userPage.getTotalPages()).isEqualTo(2);
        assertThat(userPage.getNumber()).isEqualTo(0);
        assertThat(userPage.getSize()).isEqualTo(2);
    }

    @Test
    void findByEmail_WithMatchingEmail_ShouldReturnUsers() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(testUser3);

        // When
        List<User> foundUsers = userRepository.findByEmail("john");

        // Then
        assertThat(foundUsers).hasSize(2); // Both john.doe and bob.johnson contain "john"
        assertThat(foundUsers).extracting(User::getEmail)
                .containsExactlyInAnyOrder("john.doe@example.com", "bob.johnson@example.com");
    }

    @Test
    void findByEmail_WithExactEmail_ShouldReturnUser() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);

        // When
        List<User> foundUsers = userRepository.findByEmail("jane.smith@example.com");

        // Then
        assertThat(foundUsers).hasSize(1);
        assertThat(foundUsers.get(0).getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    void findByEmail_WithNoMatch_ShouldReturnEmptyList() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);

        // When
        List<User> foundUsers = userRepository.findByEmail("nonexistent");

        // Then
        assertThat(foundUsers).isEmpty();
    }

    @Test
    void findByEmail_WithPartialMatch_ShouldReturnUsers() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(testUser3);

        // When
        List<User> foundUsers = userRepository.findByEmail("example.com");

        // Then
        assertThat(foundUsers).hasSize(3); // All users have example.com in their email
    }

    @Test
    void delete_ShouldRemoveUser() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);
        Long userId = savedUser.getId();

        // When
        userRepository.delete(savedUser);
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void update_ShouldModifyUser() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);

        // When
        savedUser.setName("John Doe Updated");
        savedUser.setEmail("john.doe.updated@example.com");
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        // Then
        assertThat(updatedUser.getName()).isEqualTo("John Doe Updated");
        assertThat(updatedUser.getEmail()).isEqualTo("john.doe.updated@example.com");
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void count_ShouldReturnTotalUsers() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);

        // When
        long count = userRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);

        // When
        boolean exists = userRepository.existsById(savedUser.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_WithNonExistingId_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void findByEmail_WithEmptyString_ShouldReturnAllUsers() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);

        // When
        List<User> foundUsers = userRepository.findByEmail("");

        // Then
        assertThat(foundUsers).hasSize(2); // All users since empty string matches all
    }

    @Test
    void save_WithUniqueConstraints_ShouldWork() {
        // When
        User savedUser = userRepository.save(testUser1);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
    }

    @Test
    void update_ShouldUpdateFieldsProperly() throws InterruptedException {
        // Given
        User savedUser = userRepository.save(testUser1);
        entityManager.flush();
        entityManager.clear();

        // When
        User userToUpdate = userRepository.findById(savedUser.getId()).orElseThrow();
        userToUpdate.setName("Updated Name");
        User updatedUser = userRepository.save(userToUpdate);
        entityManager.flush();

        // Then
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getEmail()).isEqualTo(savedUser.getEmail()); // Should remain unchanged
    }
}
