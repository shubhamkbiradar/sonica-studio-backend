package com.project.sonica.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.sonica.entity.Customer;
import com.project.sonica.entity.ServicePlan;
import com.project.sonica.repos.CustomerRepository;

@Service
public class CustomerService {
	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public Customer registerCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	public Optional<Customer> findByEmail(String email) {
		return customerRepository.findByEmail(email);
	}

	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}
	public Optional<Customer> getById(Integer id){
		return customerRepository.findById(id);
	}
}
