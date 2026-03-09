package com.project.sonica.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.BookingStatus;
import com.project.sonica.entity.Payment;
import com.project.sonica.entity.PaymentStatus;
import com.project.sonica.repos.BookingRepository;
import com.project.sonica.repos.PaymentRepository;

@Service
public class PaymentService {

	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private BookingRepository bookingRepository;

	@PreAuthorize("hasRole('CUSTOMER')")
	public Payment initiatePayment(Payment payment) {
		payment.setStatus(PaymentStatus.INITIATED);
		return paymentRepository.save(payment);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	public Payment confirmPayment(Integer paymentId) {
		Payment existing = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		existing.setStatus(PaymentStatus.SUCCESS);
		return paymentRepository.save(existing);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	public Payment retryPayment(Integer paymentId) {
		Payment existing = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		if (existing.getStatus() == PaymentStatus.FAILED) {
			existing.setStatus(PaymentStatus.INITIATED);
		}
		return paymentRepository.save(existing);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public Payment refundPayment(Integer paymentId) {
		Payment existing = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));
		existing.setStatus(PaymentStatus.REFUNDED);
		return paymentRepository.save(existing);
	}

	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	public Payment getPaymentByTransactionId(String transactionId) {
		return paymentRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new RuntimeException("Payment not found with transactionId: " + transactionId));
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	public Payment processPayment(Integer paymentId, PaymentStatus newStatus) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

		Booking booking = payment.getBooking();
		if (booking == null) {
			throw new RuntimeException("No booking associated with this payment");
		}

		// Update payment status
		payment.setStatus(newStatus);

		// Update booking status based on payment outcome
		switch (newStatus) {
		case SUCCESS:
			booking.setStatus(BookingStatus.CONFIRMED);
			break;
		case FAILED:
			booking.setStatus(BookingStatus.PENDING_PAYMENT);
			break;
		case REFUNDED:
			booking.setStatus(BookingStatus.CANCELLED);
			break;
		case AWAITING_CONFIRMATION:
			booking.setStatus(BookingStatus.PENDING_CONFIRMATION);
			break;
		default:
			booking.setStatus(BookingStatus.PENDING_PAYMENT);
		}

		// Save both entities
		bookingRepository.save(booking);
		return paymentRepository.save(payment);
	}

	@PreAuthorize("hasRole('CUSTOMER')")
	public Payment processPayment(Payment payment) {
		// Ensure the payment has a booking associated
		Booking booking = payment.getBooking();
		if (booking == null) {
			throw new RuntimeException("No booking associated with this payment");
		}

		// Update booking status based on payment outcome
		switch (payment.getStatus()) {
		case SUCCESS:
			booking.setStatus(BookingStatus.CONFIRMED);
			break;
		case FAILED:
			booking.setStatus(BookingStatus.PENDING_PAYMENT);
			break;
		case REFUNDED:
			booking.setStatus(BookingStatus.CANCELLED);
			break;
		case AWAITING_CONFIRMATION:
			booking.setStatus(BookingStatus.PENDING_CONFIRMATION);
			break;
		default:
			booking.setStatus(BookingStatus.PENDING_PAYMENT);
		}

		// Save both entities
		bookingRepository.save(booking);
		return paymentRepository.save(payment);
	}

	public Payment markAsPending(String transactionId) {
		Payment payment = paymentRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		payment.setStatus(PaymentStatus.PENDING);
		return paymentRepository.save(payment);
	}

	public Payment markAsSuccessful(String transactionId) {
		Payment payment = paymentRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		payment.setStatus(PaymentStatus.SUCCESS);
		return paymentRepository.save(payment);
	}

	// Cash payment
	public Payment requestCashPayment(String transactionId) {
		Payment payment = paymentRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		if (payment.getStatus() == PaymentStatus.FAILED || payment.getStatus() == PaymentStatus.PENDING) {
			payment.setStatus(PaymentStatus.CASH_REQUESTED);
			return paymentRepository.save(payment);
		} else {
			throw new IllegalStateException("Cash option only allowed for failed or pending payments");
		}
	}

	public Payment confirmCashPayment(String transactionId) {
		Payment payment = paymentRepository.findByTransactionId(transactionId)
				.orElseThrow(() -> new RuntimeException("Payment not found"));

		if (payment.getStatus() == PaymentStatus.CASH_REQUESTED) {
			payment.setStatus(PaymentStatus.CASH_CONFIRMED);
			return paymentRepository.save(payment);
		} else {
			throw new IllegalStateException("Cash confirmation only allowed after request");
		}
	}

}
