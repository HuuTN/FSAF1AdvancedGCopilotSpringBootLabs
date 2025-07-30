package com.example.service;

import com.example.model.entity.Customer;
import com.example.model.dto.CustomerDTO;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    Optional<Customer> getCustomerByEmail(String email);

    /**
     * Creates a new customer from the provided DTO
     * 
     * @param customerDTO the customer data
     * @return the created customer entity
     * @throws IllegalArgumentException if a customer with the same email already
     *                                  exists
     */
    Customer createCustomer(CustomerDTO customerDTO);

    /**
     * Updates an existing customer with the provided DTO data
     * 
     * @param id          the ID of the customer to update
     * @param customerDTO the updated customer data
     * @return the updated customer entity
     * @throws ResourceNotFoundException if the customer is not found
     */
    Customer updateCustomer(Long id, CustomerDTO customerDTO);

    /**
     * Deletes a customer by ID
     * 
     * @param id the ID of the customer to delete
     * @throws ResourceNotFoundException if the customer is not found
     */
    void deleteCustomer(Long id);
}
