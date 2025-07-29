package com.example.service.serviceimpl;

import com.example.model.entity.Customer;
import com.example.model.dto.CustomerDTO;
import com.example.repository.CustomerRepository;
import com.example.service.CustomerService;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public Customer createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + customerDTO.getEmail() + " already exists");
        }

        // Convert DTO to Entity using mapper
        Customer customer = customerMapper.toEntity(customerDTO);

        // Set default password if not provided
        if (customerDTO.getPassword() == null || customerDTO.getPassword().trim().isEmpty()) {
            customer.setPassword("defaultPassword123"); // In production, this should be properly hashed
        }

        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Update entity using mapper
        customerMapper.updateEntity(existingCustomer, customerDTO);

        return customerRepository.save(existingCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }
}
