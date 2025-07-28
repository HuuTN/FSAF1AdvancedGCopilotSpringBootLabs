package com.example.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.exception.ResourceNotFoundException;
import com.example.mapper.CustomerMapper;
import com.example.model.dto.CustomerDTO;
import com.example.model.entity.Customer;
import com.example.repository.CustomerRepository;
import com.example.service.serviceimpl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer testCustomer;
    private CustomerDTO testCustomerDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhone("123-456-7890");
        testCustomer.setAddress("123 Main St, City, State");

        testCustomerDTO = new CustomerDTO();
        testCustomerDTO.setName("John Doe");
        testCustomerDTO.setEmail("john.doe@example.com");
        testCustomerDTO.setPhone("123-456-7890");
        testCustomerDTO.setAddress("123 Main St, City, State");
    }

    @Nested
    @DisplayName("CRUD Operations")
    class CrudOperations {

        @Test
        @DisplayName("Should create customer successfully")
        void shouldCreateCustomerSuccessfully() {
            // Given
            CustomerDTO createDTO = new CustomerDTO();
            createDTO.setName("New Customer");
            createDTO.setEmail("new@example.com");
            createDTO.setPhone("555-0000");
            createDTO.setAddress("789 New St, City, State");
            createDTO.setPassword("password123");

            Customer newCustomer = new Customer();
            newCustomer.setName("New Customer");
            newCustomer.setEmail("new@example.com");
            newCustomer.setPhone("555-0000");
            newCustomer.setAddress("789 New St, City, State");
            newCustomer.setPassword("password123");

            when(customerRepository.existsByEmail(createDTO.getEmail())).thenReturn(false);
            when(customerMapper.toEntity(any(CustomerDTO.class))).thenReturn(newCustomer);
            when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

            // When
            Customer result = customerService.createCustomer(createDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("New Customer");
            assertThat(result.getEmail()).isEqualTo("new@example.com");
            assertThat(result.getPhone()).isEqualTo("555-0000");
            assertThat(result.getAddress()).isEqualTo("789 New St, City, State");
            verify(customerRepository).existsByEmail(createDTO.getEmail());
            verify(customerMapper).toEntity(any(CustomerDTO.class));
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should get all customers")
        void shouldGetAllCustomers() {
            // Given
            List<Customer> customers = Arrays.asList(testCustomer);
            when(customerRepository.findAll()).thenReturn(customers);

            // When
            List<Customer> result = customerService.getAllCustomers();

            // Then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("John Doe");
            verify(customerRepository).findAll();
        }

        @Test
        @DisplayName("Should get customer by ID")
        void shouldGetCustomerById() {
            // Given
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

            // When
            Optional<Customer> result = customerService.getCustomerById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("John Doe");
            verify(customerRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when customer not found by ID")
        void shouldReturnEmptyWhenCustomerNotFoundById() {
            // Given
            when(customerRepository.findById(1L)).thenReturn(Optional.empty());

            // When
            Optional<Customer> result = customerService.getCustomerById(1L);

            // Then
            assertThat(result).isEmpty();
            verify(customerRepository).findById(1L);
        }

        @Test
        @DisplayName("Should update customer successfully")
        void shouldUpdateCustomerSuccessfully() {
            // Given
            CustomerDTO updateDTO = new CustomerDTO();
            updateDTO.setName("Jane Doe");
            updateDTO.setEmail("jane.doe@example.com");
            updateDTO.setPhone("987-654-3210");
            updateDTO.setAddress("456 Oak St, City, State");

            Customer updatedCustomer = new Customer();
            updatedCustomer.setId(1L);
            updatedCustomer.setName("Jane Doe");
            updatedCustomer.setEmail("jane.doe@example.com");
            updatedCustomer.setPhone("987-654-3210");
            updatedCustomer.setAddress("456 Oak St, City, State");

            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
            when(customerMapper.updateEntity(eq(testCustomer), any(CustomerDTO.class))).thenReturn(updatedCustomer);
            when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

            // When
            Customer result = customerService.updateCustomer(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Jane Doe");
            assertThat(result.getEmail()).isEqualTo("jane.doe@example.com");
            assertThat(result.getPhone()).isEqualTo("987-654-3210");
            assertThat(result.getAddress()).isEqualTo("456 Oak St, City, State");
            verify(customerRepository).findById(1L);
            verify(customerMapper).updateEntity(eq(testCustomer), any(CustomerDTO.class));
            verify(customerRepository).save(any(Customer.class));
        }

        @Test
        @DisplayName("Should delete customer successfully")
        void shouldDeleteCustomerSuccessfully() {
            // Given
            when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

            // When
            customerService.deleteCustomer(1L);

            // Then
            verify(customerRepository).findById(1L);
            verify(customerRepository).delete(testCustomer);
        }
    }

    @Nested
    @DisplayName("Email Operations")
    class EmailOperations {

        @Test
        @DisplayName("Should find customer by email")
        void shouldFindCustomerByEmail() {
            // Given
            when(customerRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testCustomer));

            // When
            Optional<Customer> result = customerService.getCustomerByEmail("john.doe@example.com");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
            verify(customerRepository).findByEmail("john.doe@example.com");
        }

        @Test
        @DisplayName("Should return empty when customer not found by email")
        void shouldReturnEmptyWhenCustomerNotFoundByEmail() {
            // Given
            when(customerRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

            // When
            Optional<Customer> result = customerService.getCustomerByEmail("nonexistent@example.com");

            // Then
            assertThat(result).isEmpty();
            verify(customerRepository).findByEmail("nonexistent@example.com");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Validation")
    class EdgeCasesAndValidation {

        @Test
        @DisplayName("Should handle null customer DTO creation")
        void shouldHandleNullCustomerDtoCreation() {
            // When & Then
            assertThatThrownBy(() -> customerService.createCustomer(null))
                    .isInstanceOf(NullPointerException.class);

            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle null customer update")
        void shouldHandleNullCustomerUpdate() {
            // When & Then
            assertThatThrownBy(() -> customerService.updateCustomer(1L, null))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(customerRepository).findById(1L);
            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should handle customer creation with email validation")
        void shouldHandleCustomerCreationWithEmailValidation() {
            // Given
            when(customerRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> customerService.createCustomer(testCustomerDTO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Customer with email john.doe@example.com already exists");

            verify(customerRepository).existsByEmail("john.doe@example.com");
            verify(customerRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should create customer when email is unique")
        void shouldCreateCustomerWhenEmailIsUnique() {
            // Given
            when(customerRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
            when(customerMapper.toEntity(testCustomerDTO)).thenReturn(testCustomer);
            when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

            // When
            Customer result = customerService.createCustomer(testCustomerDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("John Doe");
            assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
            verify(customerRepository).existsByEmail("john.doe@example.com");
            verify(customerMapper).toEntity(testCustomerDTO);
            verify(customerRepository).save(testCustomer);
        }
    }
}
