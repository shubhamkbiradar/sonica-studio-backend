package com.project.sonica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.sonica.dto.AdminDashboard;
import com.project.sonica.dto.CustomerDashboard;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Inquiry;
import com.project.sonica.entity.Payment;
import com.project.sonica.entity.Review;
import com.project.sonica.repos.BookingRepository;
import com.project.sonica.repos.CustomerRepository;
import com.project.sonica.repos.InquiryRepository;
import com.project.sonica.repos.PaymentRepository;
import com.project.sonica.repos.ReviewRepository;

@Service
public class DashboardService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private InquiryRepository inquiryRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    public CustomerDashboard getCustomerDashboard(String email) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Customer not found"));
        List<Booking> bookings = bookingRepository.findByCustomer(customer);
        List<Payment> payments = paymentRepository.findByCustomer(customer);
        List<Review> reviews = reviewRepository.findByCustomer(customer);

        return new CustomerDashboard(bookings, payments, reviews);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public AdminDashboard getAdminDashboard() {
        List<Booking> bookings = bookingRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();
        List<Inquiry> inquiries = inquiryRepository.findAll();
        List<Review> reviews = reviewRepository.findAll();

        return new AdminDashboard(bookings, payments, inquiries, reviews);
    }
}
