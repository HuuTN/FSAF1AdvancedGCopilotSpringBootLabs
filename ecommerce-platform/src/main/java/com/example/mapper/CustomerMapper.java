package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.model.dto.CustomerDTO;
import com.example.model.entity.Customer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Customer entity and CustomerDTO
 */
@Component
public class CustomerMapper {

    /**
     * Convert Customer entity to CustomerDTO
     * 
     * @param customer The customer entity to convert
     * @return CustomerDTO representation
     */
    public CustomerDTO toDTO(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setAddress(customer.getAddress());
        dto.setPhone(customer.getPhone());
        dto.setPassword("********"); // Masked password for security
        return dto;
    }

    /**
     * Convert CustomerDTO to Customer entity
     * 
     * @param customerDTO The CustomerDTO to convert
     * @return Customer entity
     */
    public Customer toEntity(CustomerDTO customerDTO) {
        if (customerDTO == null) {
            return null;
        }

        Customer customer = new Customer();
        if (customerDTO.getId() != null) {
            customer.setId(customerDTO.getId());
        }
        customer.setName(customerDTO.getName());
        customer.setEmail(customerDTO.getEmail());
        customer.setAddress(customerDTO.getAddress());
        customer.setPhone(customerDTO.getPhone());
        if (customerDTO.getPassword() != null && !customerDTO.getPassword().trim().isEmpty()
                && !customerDTO.getPassword().equals("********")) {
            customer.setPassword(customerDTO.getPassword());
        }
        return customer;
    }

    /**
     * Update existing Customer entity with CustomerDTO data
     * 
     * @param existingCustomer The existing customer entity
     * @param customerDTO      The DTO with updated data
     * @return Updated customer entity
     */
    public Customer updateEntity(Customer existingCustomer, CustomerDTO customerDTO) {
        if (existingCustomer == null || customerDTO == null) {
            return existingCustomer;
        }

        existingCustomer.setName(customerDTO.getName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setPhone(customerDTO.getPhone());

        // Only update password if it's not the masked value
        if (customerDTO.getPassword() != null && !customerDTO.getPassword().trim().isEmpty()
                && !customerDTO.getPassword().equals("********")) {
            existingCustomer.setPassword(customerDTO.getPassword());
        }

        return existingCustomer;
    }

    /**
     * Convert list of Customer entities to list of CustomerDTOs
     * 
     * @param customers The list of customers to convert
     * @return List of CustomerDTO representations
     */
    public List<CustomerDTO> toDTOs(List<Customer> customers) {
        if (customers == null) {
            return null;
        }

        return customers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
