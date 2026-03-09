package com.project.sonica.customer.domain;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(UUID id);
    Optional<Customer> findByEmail(String email);
    boolean existsByEmail(String email);
}
