package com.example.controller;

import com.example.model.entity.Customer;
import com.example.model.dto.CustomerDTO;
import com.example.service.CustomerService;
import com.example.mapper.CustomerMapper;
import com.example.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "Operations for managing customers")
public class CustomerController {

        private final CustomerService customerService;
        private final CustomerMapper customerMapper;

        @Autowired
        public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
                this.customerService = customerService;
                this.customerMapper = customerMapper;
        }

        @Operation(summary = "Get all customers", description = "Retrieve a list of all customers")
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list of customers")
        @GetMapping
        public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
                List<Customer> customers = customerService.getAllCustomers();
                List<CustomerDTO> customerDTOs = customerMapper.toDTOs(customers);
                return ResponseEntity.ok(customerDTOs);
        }

        @Operation(summary = "Get customer by ID", description = "Retrieve a customer by their ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer found"),
                        @ApiResponse(responseCode = "404", description = "Customer not found")
        })
        @GetMapping("/{id}")
        public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
                Optional<Customer> customer = customerService.getCustomerById(id);
                return customer.map(c -> ResponseEntity.ok(customerMapper.toDTO(c)))
                                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        }

        @Operation(summary = "Get customer by email", description = "Retrieve a customer by their email address")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer found"),
                        @ApiResponse(responseCode = "404", description = "Customer not found")
        })
        @GetMapping("/email/{email}")
        public ResponseEntity<CustomerDTO> getCustomerByEmail(@PathVariable String email) {
                Optional<Customer> customer = customerService.getCustomerByEmail(email);
                return customer.map(c -> ResponseEntity.ok(customerMapper.toDTO(c)))
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Customer not found with email: " + email));
        }

        @Operation(summary = "Create a new customer", description = "Create a new customer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Customer created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid input")
        })
        @PostMapping(consumes = "application/json", produces = "application/json")
        public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
                Customer createdCustomer = customerService.createCustomer(customerDTO);
                return ResponseEntity.status(HttpStatus.CREATED).body(customerMapper.toDTO(createdCustomer));
        }

        @Operation(summary = "Update customer", description = "Update an existing customer")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Customer not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid input")
        })
        @PutMapping(value = "/{id}", consumes = "application/json", produces = "application/json")
        public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id,
                        @Valid @RequestBody CustomerDTO customerDTO) {
                Customer updatedCustomer = customerService.updateCustomer(id, customerDTO);
                return ResponseEntity.ok(customerMapper.toDTO(updatedCustomer));
        }

        @Operation(summary = "Delete customer", description = "Delete a customer by ID")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Customer not found")
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
                customerService.deleteCustomer(id);
                return ResponseEntity.noContent().build();
        }
}
