package com.project.sonica.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Payment;
import com.project.sonica.entity.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	Optional<Payment> findByTransactionId(String transactionId);

	List<Payment> findByStatus(PaymentStatus status);

	List<Payment> findByBookingCustomer(Customer customer);

	long countByStatus(PaymentStatus status);

}
