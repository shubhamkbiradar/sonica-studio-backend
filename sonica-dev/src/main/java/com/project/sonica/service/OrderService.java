package com.project.sonica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Contract;
import com.project.sonica.entity.Order;
import com.project.sonica.entity.OrderStatus;
import com.project.sonica.entity.Payment;
import com.project.sonica.repos.BookingRepository;
import com.project.sonica.repos.ContractRepository;
import com.project.sonica.repos.PaymentRepository;

@Service
public class OrderService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ContractRepository contractRepository;

    @PreAuthorize("hasRole('CUSTOMER')")
    public Order createOrder(Integer bookingId, Integer paymentId, Integer contractId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        Order order = new Order();
        order.setBooking(booking);
        order.setPayment(payment);
        order.setContract(contract);
        order.setStatus(OrderStatus.CONFIRMED);

        // Save via OrderRepository (assumed)
        return order;
    }
}
