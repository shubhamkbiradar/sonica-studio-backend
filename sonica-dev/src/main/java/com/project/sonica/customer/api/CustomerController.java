package com.project.sonica.customer.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.customer.application.CustomerService;
import com.project.sonica.customer.domain.Customer;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<Customer> register(@RequestBody RegisterCustomerRequest request) {
        Customer customer = customerService.registerCustomer(
                request.getName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getPasswordHash()
        );
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable UUID id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<Customer> updateProfile(@PathVariable UUID id, @RequestBody UpdateProfileRequest request) {
        Customer customer = customerService.updateCustomerProfile(id, request.getName(), request.getPhoneNumber());
        return ResponseEntity.ok(customer);
    }

    // DTOs
    public static class RegisterCustomerRequest {
        private String name;
        private String email;
        private String phoneNumber;
        private String passwordHash;
        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public String getPasswordHash() { return passwordHash; }
        public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    }

    public static class UpdateProfileRequest {
        private String name;
        private String phoneNumber;
        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }
}
