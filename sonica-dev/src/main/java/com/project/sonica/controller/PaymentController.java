package com.project.sonica.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.dto.PaymentRequest;
import com.project.sonica.dto.PaymentResponse;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Payment;
import com.project.sonica.entity.PaymentStatus;
import com.project.sonica.mapper.PaymentMapper;
import com.project.sonica.service.BookingService;
import com.project.sonica.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	@Autowired
	private PaymentService paymentService;
	@Autowired
	private PaymentMapper paymentMapper;
	@Autowired
	private BookingService bookingService;

	@PostMapping
	public ResponseEntity<PaymentResponse> process(@RequestBody PaymentRequest request) {
		Booking booking = bookingService.getById(request.getBookingId())
				.orElseThrow(() -> new RuntimeException("Booking not found"));
		Payment payment = paymentMapper.toEntity(request, booking);
		Payment saved = paymentService.processPayment(payment);
		return ResponseEntity.ok(paymentMapper.toResponse(saved));
	}

//	@GetMapping("/transaction/{transactionId}")
//	public ResponseEntity<PaymentResponse> getByTransaction(@PathVariable String transactionId) {
//		return Optional.ofNullable(paymentService.getPaymentByTransactionId(transactionId))
//				.map(paymentMapper::toResponse).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
//	}

//	@GetMapping("/transaction/{transactionId}")
//	public ResponseEntity<PaymentResponse> getByTransaction(@PathVariable String transactionId) {
//		return Optional.ofNullable(paymentService.getPaymentByTransactionId(transactionId)).map(payment -> {
//			if (payment.getStatus() == PaymentStatus.PENDING) {
//				return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentMapper.toResponse(payment));
//			} else if (payment.getStatus() == PaymentStatus.SUCCESS) {
//				return ResponseEntity.ok(paymentMapper.toResponse(payment));
//			} else {
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(paymentMapper.toResponse(payment));
//			}
//		}).orElse(ResponseEntity.notFound().build());
//	}

	@GetMapping("/transaction/{transactionId}")
	public ResponseEntity<PaymentResponse> getByTransaction(@PathVariable String transactionId) {
		return Optional.ofNullable(paymentService.getPaymentByTransactionId(transactionId)).map(payment -> {
			PaymentResponse response = paymentMapper.toResponse(payment);

			if (payment.getStatus() == PaymentStatus.PENDING || payment.getStatus() == PaymentStatus.FAILED) {
				response.setAlternativePaymentOption("Cash payment available with confirmation");
			} else if (payment.getStatus() == PaymentStatus.CASH_REQUESTED) {
				response.setAlternativePaymentOption("Awaiting cashier confirmation");
			}

			return ResponseEntity.ok(response);
		}).orElse(ResponseEntity.notFound().build());
	}
//	@GetMapping("/transaction/{transactionId}")
//	public ResponseEntity<PaymentResponse> getByTransaction(@PathVariable String transactionId) {
//	    return paymentService.getPaymentByTransactionId(transactionId)
//	            .map(payment -> {
//	                if (payment.getStatus() == PaymentStatus.PENDING) {
//	                    // custom logic for pending
//	                    return ResponseEntity.status(HttpStatus.ACCEPTED)
//	                            .body(paymentMapper.toResponse(payment));
//	                }
//	                return ResponseEntity.ok(paymentMapper.toResponse(payment));
//	            })
//	            .orElse(ResponseEntity.notFound().build());
//	}

//	@GetMapping("/transaction/{transactionId}")
////	public ResponseEntity<PaymentResponse> getByTransaction(@PathVariable String transactionId) {
//		return paymentService.getPaymentByTransactionId(transactionId).map(payment -> {
//			PaymentResponse response = paymentMapper.toResponse(payment);
//
//			if (payment.getStatus() == PaymentStatus.PENDING || payment.getStatus() == PaymentStatus.FAILED) {
//				response.setAlternativePaymentOption("Cash payment available with confirmation");
//			}
//
//			return ResponseEntity.ok(response);
//		}).orElse(ResponseEntity.notFound().build());
//	}

	@PutMapping("/transaction/{transactionId}/pending")
	public ResponseEntity<PaymentResponse> markPending(@PathVariable String transactionId) {
		Payment payment = paymentService.markAsPending(transactionId);
		return ResponseEntity.ok(paymentMapper.toResponse(payment));
	}

	@PutMapping("/transaction/{transactionId}/success")
	public ResponseEntity<PaymentResponse> markSuccessful(@PathVariable String transactionId) {
		Payment payment = paymentService.markAsSuccessful(transactionId);
		return ResponseEntity.ok(paymentMapper.toResponse(payment));
	}

	// Customer requests cash payment
	@PutMapping("/transaction/{transactionId}/cash-request")
	public ResponseEntity<PaymentResponse> requestCash(@PathVariable String transactionId) {
		try {
			Payment payment = paymentService.requestCashPayment(transactionId);
			return ResponseEntity.ok(paymentMapper.toResponse(payment));
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	// Admin/cashier confirms cash payment
	@PutMapping("/transaction/{transactionId}/cash-confirm")
	public ResponseEntity<PaymentResponse> confirmCash(@PathVariable String transactionId) {
		try {
			Payment payment = paymentService.confirmCashPayment(transactionId);
			return ResponseEntity.ok(paymentMapper.toResponse(payment));
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

}
