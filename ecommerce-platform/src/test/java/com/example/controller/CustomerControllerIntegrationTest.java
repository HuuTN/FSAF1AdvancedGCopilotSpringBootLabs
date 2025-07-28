package com.example.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.exception.ResourceNotFoundException;
import com.example.mapper.CustomerMapper;
import com.example.model.dto.CustomerDTO;
import com.example.model.entity.Customer;
import com.example.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CustomerController.class)
@DisplayName("Customer Controller Integration Tests")
class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerMapper customerMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomerDTO testCustomerDTO;
    private Customer testCustomer;
    private List<CustomerDTO> customerDTOList;
    private List<Customer> customerList;

    @BeforeEach
    void setUp() {
        // Setup test customer entity
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setAddress("123 Main St, Anytown, USA");
        testCustomer.setPhone("555-1234");
        testCustomer.setPassword("hashedPassword123");

        // Setup test customer DTO with masked password
        testCustomerDTO = new CustomerDTO();
        testCustomerDTO.setId(1L);
        testCustomerDTO.setName("John Doe");
        testCustomerDTO.setEmail("john.doe@example.com");
        testCustomerDTO.setAddress("123 Main St, Anytown, USA");
        testCustomerDTO.setPhone("555-1234");
        testCustomerDTO.setPassword("********"); // Masked password

        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Jane Smith");
        customer2.setEmail("jane.smith@example.com");
        customer2.setAddress("456 Oak Ave, Another City, USA");
        customer2.setPhone("555-5678");
        customer2.setPassword("hashedPassword456");

        CustomerDTO customerDTO2 = new CustomerDTO();
        customerDTO2.setId(2L);
        customerDTO2.setName("Jane Smith");
        customerDTO2.setEmail("jane.smith@example.com");
        customerDTO2.setAddress("456 Oak Ave, Another City, USA");
        customerDTO2.setPhone("555-5678");
        customerDTO2.setPassword("********"); // Masked password

        customerList = Arrays.asList(testCustomer, customer2);
        customerDTOList = Arrays.asList(testCustomerDTO, customerDTO2);
    }

    @Nested
    class GetAllCustomersTests {

        @Test
        void getAllCustomers_ShouldReturnListOfCustomers() throws Exception {
            // Given
            when(customerService.getAllCustomers()).thenReturn(customerList);
            when(customerMapper.toDTOs(customerList)).thenReturn(customerDTOList);

            // When & Then
            mockMvc.perform(get("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("John Doe")))
                    .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
                    .andExpect(jsonPath("$[0].address", is("123 Main St, Anytown, USA")))
                    .andExpect(jsonPath("$[0].phone", is("555-1234")))
                    .andExpect(jsonPath("$[0].password", is("********"))) // Password is masked
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].name", is("Jane Smith")))
                    .andExpect(jsonPath("$[1].email", is("jane.smith@example.com")))
                    .andExpect(jsonPath("$[1].password", is("********"))); // Password is masked

            verify(customerService, times(1)).getAllCustomers();
            verify(customerMapper, times(1)).toDTOs(customerList);
        }

        @Test
        void getAllCustomers_WhenNoCustomersExist_ShouldReturnEmptyArray() throws Exception {
            // Given
            when(customerService.getAllCustomers()).thenReturn(Arrays.asList());
            when(customerMapper.toDTOs(any())).thenReturn(Arrays.asList());

            // When & Then
            mockMvc.perform(get("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(customerService, times(1)).getAllCustomers();
        }
    }

    @Nested
    class GetCustomerByIdTests {

        @Test
        void getCustomerById_WithValidId_ShouldReturnCustomer() throws Exception {
            // Given
            when(customerService.getCustomerById(1L)).thenReturn(Optional.of(testCustomer));
            when(customerMapper.toDTO(testCustomer)).thenReturn(testCustomerDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/customers/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                    .andExpect(jsonPath("$.address", is("123 Main St, Anytown, USA")))
                    .andExpect(jsonPath("$.phone", is("555-1234")))
                    .andExpect(jsonPath("$.password", is("********"))); // Password is masked

            verify(customerService, times(1)).getCustomerById(1L);
            verify(customerMapper, times(1)).toDTO(testCustomer);
        }

        @Test
        void getCustomerById_WithInvalidId_ShouldThrowResourceNotFoundException() throws Exception {
            // Given
            when(customerService.getCustomerById(999L)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/customers/999")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Resource Not Found")))
                    .andExpect(jsonPath("$.message", containsString("Customer not found with id: 999")));

            verify(customerService, times(1)).getCustomerById(999L);
            verify(customerMapper, never()).toDTO(any());
        }
    }

    @Nested
    class GetCustomerByEmailTests {

        @Test
        void getCustomerByEmail_WithValidEmail_ShouldReturnCustomer() throws Exception {
            // Given
            String email = "john.doe@example.com";
            when(customerService.getCustomerByEmail(email)).thenReturn(Optional.of(testCustomer));
            when(customerMapper.toDTO(testCustomer)).thenReturn(testCustomerDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/customers/email/{email}", email)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                    .andExpect(jsonPath("$.address", is("123 Main St, Anytown, USA")))
                    .andExpect(jsonPath("$.phone", is("555-1234")))
                    .andExpect(jsonPath("$.password", is("********"))); // Password is masked

            verify(customerService, times(1)).getCustomerByEmail(email);
            verify(customerMapper, times(1)).toDTO(testCustomer);
        }

        @Test
        void getCustomerByEmail_WithInvalidEmail_ShouldThrowResourceNotFoundException() throws Exception {
            // Given
            String email = "nonexistent@example.com";
            when(customerService.getCustomerByEmail(email)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/v1/customers/email/{email}", email)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Resource Not Found")))
                    .andExpect(jsonPath("$.message", containsString("Customer not found with email: " + email)));

            verify(customerService, times(1)).getCustomerByEmail(email);
            verify(customerMapper, never()).toDTO(any());
        }
    }

    @Nested
    class CreateCustomerTests {

        @Test
        void createCustomer_WithValidCustomerDTO_ShouldReturnCreatedCustomer() throws Exception {
            // Given
            CustomerDTO inputCustomerDTO = new CustomerDTO();
            inputCustomerDTO.setName("John Doe");
            inputCustomerDTO.setEmail("john.doe@example.com");
            inputCustomerDTO.setAddress("123 Main St, Anytown, USA");
            inputCustomerDTO.setPhone("555-1234");
            inputCustomerDTO.setPassword("password123"); // Real password in request

            when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(testCustomer);
            when(customerMapper.toDTO(testCustomer)).thenReturn(testCustomerDTO);

            // When & Then
            mockMvc.perform(post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(inputCustomerDTO)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                    .andExpect(jsonPath("$.address", is("123 Main St, Anytown, USA")))
                    .andExpect(jsonPath("$.phone", is("555-1234")))
                    .andExpect(jsonPath("$.password", is("********"))); // Password should be masked in response

            verify(customerService, times(1)).createCustomer(any(CustomerDTO.class));
            verify(customerMapper, times(1)).toDTO(testCustomer);
        }

        @Test
        void createCustomer_WithInvalidCustomerDTO_ShouldReturnBadRequest() throws Exception {
            // Given - Invalid customer DTO with missing required fields
            CustomerDTO invalidCustomerDTO = new CustomerDTO();
            invalidCustomerDTO.setName(""); // Empty name
            invalidCustomerDTO.setEmail("invalid-email"); // Invalid email format
            invalidCustomerDTO.setAddress(""); // Empty address
            invalidCustomerDTO.setPhone(""); // Empty phone

            // When & Then
            mockMvc.perform(post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCustomerDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")));

            verify(customerService, never()).createCustomer(any(CustomerDTO.class));
        }

        @Test
        void createCustomer_WithEmptyRequestBody_ShouldReturnBadRequest() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(customerService, never()).createCustomer(any(CustomerDTO.class));
        }
    }

    @Nested
    class UpdateCustomerTests {

        @Test
        void updateCustomer_WithValidData_ShouldReturnUpdatedCustomer() throws Exception {
            // Given
            CustomerDTO updateCustomerDTO = new CustomerDTO();
            updateCustomerDTO.setName("John Doe Updated");
            updateCustomerDTO.setEmail("john.doe.updated@example.com");
            updateCustomerDTO.setAddress("456 Updated St, New City, USA");
            updateCustomerDTO.setPhone("555-9999");
            updateCustomerDTO.setPassword("newPassword123");

            Customer updatedCustomer = testCustomer;
            updatedCustomer.setName("John Doe Updated");
            updatedCustomer.setEmail("john.doe.updated@example.com");
            updatedCustomer.setAddress("456 Updated St, New City, USA");
            updatedCustomer.setPhone("555-9999");
            updatedCustomer.setPassword("hashedNewPassword123");

            CustomerDTO returnedCustomerDTO = new CustomerDTO();
            returnedCustomerDTO.setId(1L);
            returnedCustomerDTO.setName("John Doe Updated");
            returnedCustomerDTO.setEmail("john.doe.updated@example.com");
            returnedCustomerDTO.setAddress("456 Updated St, New City, USA");
            returnedCustomerDTO.setPhone("555-9999");
            returnedCustomerDTO.setPassword("********"); // Masked password

            when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(updatedCustomer);
            when(customerMapper.toDTO(updatedCustomer)).thenReturn(returnedCustomerDTO);

            // When & Then
            mockMvc.perform(put("/api/v1/customers/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateCustomerDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe Updated")))
                    .andExpect(jsonPath("$.email", is("john.doe.updated@example.com")))
                    .andExpect(jsonPath("$.address", is("456 Updated St, New City, USA")))
                    .andExpect(jsonPath("$.phone", is("555-9999")))
                    .andExpect(jsonPath("$.password", is("********"))); // Password should be masked

            verify(customerService, times(1)).updateCustomer(eq(1L), any(CustomerDTO.class));
            verify(customerMapper, times(1)).toDTO(updatedCustomer);
        }

        @Test
        void updateCustomer_WithInvalidId_ShouldReturnNotFound() throws Exception {
            // Given
            CustomerDTO updateCustomerDTO = new CustomerDTO();
            updateCustomerDTO.setName("John Doe Updated");
            updateCustomerDTO.setEmail("john.doe.updated@example.com");
            updateCustomerDTO.setAddress("456 Updated St, New City, USA");
            updateCustomerDTO.setPhone("555-9999");

            when(customerService.updateCustomer(eq(999L), any(CustomerDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Customer not found with id: 999"));

            // When & Then
            mockMvc.perform(put("/api/v1/customers/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateCustomerDTO)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Resource Not Found")))
                    .andExpect(jsonPath("$.message", containsString("Customer not found with id: 999")));

            verify(customerService, times(1)).updateCustomer(eq(999L), any(CustomerDTO.class));
        }

        @Test
        void updateCustomer_WithInvalidCustomerDTO_ShouldReturnBadRequest() throws Exception {
            // Given - Invalid customer DTO with missing required fields
            CustomerDTO invalidCustomerDTO = new CustomerDTO();
            invalidCustomerDTO.setName(""); // Empty name
            invalidCustomerDTO.setEmail("invalid-email"); // Invalid email format
            invalidCustomerDTO.setAddress(""); // Empty address
            invalidCustomerDTO.setPhone(""); // Empty phone

            // When & Then
            mockMvc.perform(put("/api/v1/customers/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCustomerDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")));

            verify(customerService, never()).updateCustomer(any(Long.class), any(CustomerDTO.class));
        }
    }

    @Nested
    class DeleteCustomerTests {

        @Test
        void deleteCustomer_WithValidId_ShouldReturnNoContent() throws Exception {
            // Given
            doNothing().when(customerService).deleteCustomer(1L);

            // When & Then
            mockMvc.perform(delete("/api/v1/customers/1")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());

            verify(customerService, times(1)).deleteCustomer(1L);
        }

        @Test
        void deleteCustomer_WithInvalidId_ShouldReturnNotFound() throws Exception {
            // Given
            doThrow(new ResourceNotFoundException("Customer not found with id: 999"))
                    .when(customerService).deleteCustomer(999L);

            // When & Then
            mockMvc.perform(delete("/api/v1/customers/999")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.error", is("Resource Not Found")))
                    .andExpect(jsonPath("$.message", containsString("Customer not found with id: 999")));

            verify(customerService, times(1)).deleteCustomer(999L);
        }
    }

    @Nested
    class EdgeCasesAndDataValidationTests {

        @Test
        void createCustomer_WithExcessivelyLongName_ShouldReturnBadRequest() throws Exception {
            // Given - Customer with name exceeding 100 characters
            CustomerDTO invalidCustomerDTO = new CustomerDTO();
            invalidCustomerDTO.setName("A".repeat(101)); // 101 characters, exceeds max of 100
            invalidCustomerDTO.setEmail("valid@example.com");
            invalidCustomerDTO.setAddress("123 Main St");
            invalidCustomerDTO.setPhone("555-1234");

            // When & Then
            mockMvc.perform(post("/api/v1/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidCustomerDTO)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status", is(400)))
                    .andExpect(jsonPath("$.error", is("Validation Failed")));

            verify(customerService, never()).createCustomer(any(CustomerDTO.class));
        }

        @Test
        void getCustomerByEmail_WithSpecialCharactersInEmail_ShouldWork() throws Exception {
            // Given
            String specialEmail = "user+test@example-domain.com";
            when(customerService.getCustomerByEmail(specialEmail)).thenReturn(Optional.of(testCustomer));
            when(customerMapper.toDTO(testCustomer)).thenReturn(testCustomerDTO);

            // When & Then
            mockMvc.perform(get("/api/v1/customers/email/{email}", specialEmail)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")));

            verify(customerService, times(1)).getCustomerByEmail(specialEmail);
        }

        @Test
        void updateCustomer_WithNullOptionalFields_ShouldWork() throws Exception {
            // Given
            CustomerDTO updateCustomerDTO = new CustomerDTO();
            updateCustomerDTO.setName("John Doe Updated");
            updateCustomerDTO.setEmail("john.doe.updated@example.com");
            updateCustomerDTO.setAddress("456 Updated St, New City, USA");
            updateCustomerDTO.setPhone("555-9999");
            updateCustomerDTO.setPassword(null); // Null password should not update

            when(customerService.updateCustomer(eq(1L), any(CustomerDTO.class))).thenReturn(testCustomer);
            when(customerMapper.toDTO(testCustomer)).thenReturn(testCustomerDTO);

            // When & Then
            mockMvc.perform(put("/api/v1/customers/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateCustomerDTO)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.password", is("********"))); // Should remain masked

            verify(customerService, times(1)).updateCustomer(eq(1L), any(CustomerDTO.class));
        }
    }
}
