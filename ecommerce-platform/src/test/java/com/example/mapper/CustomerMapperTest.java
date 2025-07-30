package com.example.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.model.dto.CustomerDTO;
import com.example.model.entity.Customer;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Mapper Tests")
class CustomerMapperTest {

    @InjectMocks
    private CustomerMapper customerMapper;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john.doe@example.com");
        customer.setAddress("123 Main St");
        customer.setPhone("555-1234");
        customer.setPassword("hashedPassword123");

        customerDTO = new CustomerDTO();
        customerDTO.setId(1L);
        customerDTO.setName("John Doe");
        customerDTO.setEmail("john.doe@example.com");
        customerDTO.setAddress("123 Main St");
        customerDTO.setPhone("555-1234");
        customerDTO.setPassword("newPassword123");
    }

    @Nested
    @DisplayName("Entity to DTO Conversion Tests")
    class EntityToDtoTests {

        @Test
        @DisplayName("Should convert Customer to CustomerDTO with masked password")
        void shouldConvertCustomerToCustomerDTO() {
            // When
            CustomerDTO dto = customerMapper.toDTO(customer);

            // Then
            assertThat(dto).isNotNull();
            assertThat(dto.getId()).isEqualTo(customer.getId());
            assertThat(dto.getName()).isEqualTo(customer.getName());
            assertThat(dto.getEmail()).isEqualTo(customer.getEmail());
            assertThat(dto.getAddress()).isEqualTo(customer.getAddress());
            assertThat(dto.getPhone()).isEqualTo(customer.getPhone());
            assertThat(dto.getPassword()).isEqualTo("********"); // Password should be masked
        }

        @Test
        @DisplayName("Should handle null Customer in toDTO")
        void shouldHandleNullCustomerInToDTO() {
            // When
            CustomerDTO dto = customerMapper.toDTO(null);

            // Then
            assertThat(dto).isNull();
        }
    }

    @Nested
    @DisplayName("DTO to Entity Conversion Tests")
    class DtoToEntityTests {

        @Test
        @DisplayName("Should convert CustomerDTO to Customer")
        void shouldConvertCustomerDTOToCustomer() {
            // When
            Customer entity = customerMapper.toEntity(customerDTO);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isEqualTo(customerDTO.getId());
            assertThat(entity.getName()).isEqualTo(customerDTO.getName());
            assertThat(entity.getEmail()).isEqualTo(customerDTO.getEmail());
            assertThat(entity.getAddress()).isEqualTo(customerDTO.getAddress());
            assertThat(entity.getPhone()).isEqualTo(customerDTO.getPhone());
            assertThat(entity.getPassword()).isEqualTo(customerDTO.getPassword());
        }

        @Test
        @DisplayName("Should handle null CustomerDTO in toEntity")
        void shouldHandleNullCustomerDTOInToEntity() {
            // When
            Customer entity = customerMapper.toEntity(null);

            // Then
            assertThat(entity).isNull();
        }

        @Test
        @DisplayName("Should handle null fields in CustomerDTO")
        void shouldHandleNullFieldsInCustomerDTO() {
            // Given
            CustomerDTO emptyDTO = new CustomerDTO();

            // When
            Customer entity = customerMapper.toEntity(emptyDTO);

            // Then
            assertThat(entity).isNotNull();
            assertThat(entity.getId()).isNull();
            assertThat(entity.getName()).isNull();
            assertThat(entity.getEmail()).isNull();
            assertThat(entity.getAddress()).isNull();
            assertThat(entity.getPhone()).isNull();
            assertThat(entity.getPassword()).isNull();
        }
    }

    @Nested
    @DisplayName("Update Entity Tests")
    class UpdateEntityTests {

        @Test
        @DisplayName("Should update Customer with CustomerDTO data")
        void shouldUpdateCustomerWithCustomerDTOData() {
            // Given
            Customer existingCustomer = new Customer();
            existingCustomer.setId(1L);
            existingCustomer.setName("Old Name");
            existingCustomer.setEmail("old.email@example.com");
            existingCustomer.setAddress("Old Address");
            existingCustomer.setPhone("000-0000");
            existingCustomer.setPassword("oldPassword");

            CustomerDTO updateDTO = new CustomerDTO();
            updateDTO.setName("New Name");
            updateDTO.setEmail("new.email@example.com");
            updateDTO.setAddress("New Address");
            updateDTO.setPhone("111-1111");
            updateDTO.setPassword("newPassword123");

            // When
            Customer updatedCustomer = customerMapper.updateEntity(existingCustomer, updateDTO);

            // Then
            assertThat(updatedCustomer).isNotNull();
            assertThat(updatedCustomer.getId()).isEqualTo(existingCustomer.getId());
            assertThat(updatedCustomer.getName()).isEqualTo(updateDTO.getName());
            assertThat(updatedCustomer.getEmail()).isEqualTo(updateDTO.getEmail());
            assertThat(updatedCustomer.getAddress()).isEqualTo(updateDTO.getAddress());
            assertThat(updatedCustomer.getPhone()).isEqualTo(updateDTO.getPhone());
            assertThat(updatedCustomer.getPassword()).isEqualTo(updateDTO.getPassword());
        }

        @Test
        @DisplayName("Should not update password when masked value provided")
        void shouldNotUpdatePasswordWhenMaskedValueProvided() {
            // Given
            Customer existingCustomer = new Customer();
            existingCustomer.setPassword("originalPassword123");

            CustomerDTO updateDTO = new CustomerDTO();
            updateDTO.setPassword("********");

            // When
            Customer updatedCustomer = customerMapper.updateEntity(existingCustomer, updateDTO);

            // Then
            assertThat(updatedCustomer.getPassword()).isEqualTo("originalPassword123");
        }

        @Test
        @DisplayName("Should handle null CustomerDTO in updateEntity")
        void shouldHandleNullCustomerDTOInUpdateEntity() {
            // Given
            Customer existingCustomer = new Customer();
            existingCustomer.setId(1L);
            existingCustomer.setName("Original Name");

            // When
            Customer updatedCustomer = customerMapper.updateEntity(existingCustomer, null);

            // Then
            assertThat(updatedCustomer).isEqualTo(existingCustomer);
        }
    }
}
