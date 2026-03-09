package com.project.sonica.customer.application;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.sonica.customer.domain.Customer;
import com.project.sonica.customer.domain.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer registerCustomer(String name, String email, String phoneNumber, String passwordHash) {
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer.setPhoneNumber(phoneNumber);
        customer.setPasswordHash(passwordHash);
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Customer updateCustomerProfile(UUID id, String name, String phoneNumber) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        customer.updateProfile(name, phoneNumber);
        return customerRepository.save(customer);
    }
}
