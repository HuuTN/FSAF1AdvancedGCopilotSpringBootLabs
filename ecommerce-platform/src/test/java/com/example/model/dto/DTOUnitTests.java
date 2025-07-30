package com.example.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.mapper.UserMapper;

import com.example.model.entity.User;
import com.example.model.enums.OrderStatus;
import com.example.model.enums.UserRole;

@SpringBootTest
@DisplayName("DTO Unit Tests")
class DTOUnitTests {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserMapper userMapper() {
            return new UserMapper();
        }
    }

    @Autowired
    private UserMapper userMapper;

    @Nested
    @DisplayName("CustomerDTO Tests")
    class CustomerDTOTests {
        @Test
        @DisplayName("Should create CustomerDTO with all fields")
        void shouldCreateCustomerDTOWithAllFields() {
            // Given
            CustomerDTO dto = new CustomerDTO();
            dto.setId(1L);
            dto.setName("John Doe");
            dto.setEmail("john@example.com");
            dto.setAddress("123 Main St");
            dto.setPhone("123-456-7890");
            dto.setPassword("password123");

            // Then
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getName()).isEqualTo("John Doe");
            assertThat(dto.getEmail()).isEqualTo("john@example.com");
            assertThat(dto.getAddress()).isEqualTo("123 Main St");
            assertThat(dto.getPhone()).isEqualTo("123-456-7890");
            assertThat(dto.getPassword()).isEqualTo("password123");
        }

        @Test
        @DisplayName("Should create CustomerDTO with null fields")
        void shouldCreateCustomerDTOWithNullFields() {
            // Given
            CustomerDTO dto = new CustomerDTO();

            // Then
            assertThat(dto.getId()).isNull();
            assertThat(dto.getName()).isNull();
            assertThat(dto.getEmail()).isNull();
            assertThat(dto.getAddress()).isNull();
            assertThat(dto.getPhone()).isNull();
            assertThat(dto.getPassword()).isNull();
        }
    }

    @Nested
    @DisplayName("OrderInfoDTO Tests")
    class OrderInfoDTOTests {
        @Test
        @DisplayName("Should create OrderInfoDTO with all fields")
        void shouldCreateOrderInfoDTOWithAllFields() {
            // Given
            List<OrderInfoDTO.ProductInfo> products = new ArrayList<>();
            products.add(new OrderInfoDTO.ProductInfo(1L, "Product 1", "Category 1", 2, "99.99"));

            // When
            OrderInfoDTO dto = new OrderInfoDTO(
                    1L, "John Doe", "john@example.com", "John Doe",
                    new BigDecimal("199.98"), OrderStatus.PENDING, products);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getOrderId()).isEqualTo(1L);
            assertThat(dto.getCustomerName()).isEqualTo("John Doe");
            assertThat(dto.getCustomerEmail()).isEqualTo("john@example.com");
            assertThat(dto.getOrderedBy()).isEqualTo("John Doe");
            assertThat(dto.getTotalAmount()).isEqualTo(new BigDecimal("199.98"));
            assertThat(dto.getStatus()).isEqualTo(OrderStatus.PENDING);
            assertThat(dto.getProducts()).hasSize(1);
            assertThat(dto.getProducts().get(0).getProductId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("ReviewDTO Tests")
    class ReviewDTOTests {
        @Test
        @DisplayName("Should create ReviewDTO with all fields")
        void shouldCreateReviewDTOWithAllFields() {
            // Given
            LocalDateTime now = LocalDateTime.now();

            // When
            ReviewDTO dto = new ReviewDTO(1L, 1L, "Product 1", 1L, "Customer 1", 5,
                    "Great product!", now);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(1L);
            assertThat(dto.getProductId()).isEqualTo(1L);
            assertThat(dto.getProductName()).isEqualTo("Product 1");
            assertThat(dto.getCustomerId()).isEqualTo(1L);
            assertThat(dto.getCustomerName()).isEqualTo("Customer 1");
            assertThat(dto.getRating()).isEqualTo(5);
            assertThat(dto.getReviewContent()).isEqualTo("Great product!");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("UserDTO Tests")
    class UserDTOTests {
        @Test
        @DisplayName("Should convert from User entity to DTO")
        void shouldConvertFromUserEntityToDTO() {
            // Given
            User user = new User();
            user.setId(1L);
            user.setName("John Doe");
            user.setEmail("john@example.com");
            user.setPassword("secret123");
            user.setRole(UserRole.ADMIN);

            // When
            UserDTO dto = userMapper.toDTO(user);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(user.getId());
            assertThat(dto.getName()).isEqualTo(user.getName());
            assertThat(dto.getEmail()).isEqualTo(user.getEmail());
            assertThat(dto.getPassword()).isEqualTo("********"); // Password should be masked
            assertThat(dto.getRole()).isEqualTo(user.getRole());
        }

        @Test
        @DisplayName("Should convert from DTO to User entity")
        void shouldConvertFromDTOToUserEntity() {
            // Given
            UserDTO dto = UserDTO.builder()
                    .id(1L)
                    .name("John Doe")
                    .email("john@example.com")
                    .password("newpassword123")
                    .role(UserRole.ADMIN)
                    .build();

            // When
            User user = userMapper.toEntity(dto);

            // Then
            assertThat(user).isNotNull();
            assertThat(user.getId()).isEqualTo(dto.getId());
            assertThat(user.getName()).isEqualTo(dto.getName());
            assertThat(user.getEmail()).isEqualTo(dto.getEmail());
            assertThat(user.getPassword()).isEqualTo(dto.getPassword());
            assertThat(user.getRole()).isEqualTo(dto.getRole());
        }

        @Test
        @DisplayName("Should handle null input in fromEntity")
        void shouldHandleNullInputInFromEntity() {
            // When
            UserDTO dto = userMapper.toDTO(null);

            // Then
            assertThat(dto).isNull();
        }

        @Test
        @DisplayName("Should handle null input in toEntity")
        void shouldHandleNullInputInToEntity() {
            // When
            User user = userMapper.toEntity(null);

            // Then
            assertThat(user).isNull();
        }
    }
}
