package com.project.sonica.mapper;

import org.springframework.stereotype.Component;

import com.project.sonica.dto.CustomerRequest;
import com.project.sonica.dto.CustomerResponse;
import com.project.sonica.entity.Customer;

@Component
public class CustomerMapper {
	public CustomerResponse toResponse(Customer customer) {
		CustomerResponse dto = new CustomerResponse();
		dto.setCustomerId(customer.getCustomerId());
		dto.setName(customer.getName());
		dto.setEmail(customer.getEmail());
		dto.setPhoneNumber(customer.getPhoneNumber());
		dto.setRole(customer.getRole());
		return dto;
	}

	public Customer toEntity(CustomerRequest request) {
		Customer customer = new Customer();
		customer.setName(request.getName());
		customer.setEmail(request.getEmail());
		customer.setPhoneNumber(request.getPhoneNumber());
		customer.setPasswordHash(request.getPassword()); // hash later in service
		customer.setRole("Customer");
		return customer;
	}
}
