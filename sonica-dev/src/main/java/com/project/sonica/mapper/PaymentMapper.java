package com.project.sonica.mapper;

import org.springframework.stereotype.Component;

import com.project.sonica.dto.PaymentRequest;
import com.project.sonica.dto.PaymentResponse;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Payment;
import com.project.sonica.entity.PaymentStatus;

@Component
public class PaymentMapper {
	public PaymentResponse toResponse(Payment payment) {
		PaymentResponse response = new PaymentResponse();

		response.setStatus(payment.getStatus());
		response.setPaymentId(payment.getPaymentId());
		response.setAmount(payment.getAmount());
		response.setPaymentMethod(payment.getPaymentMethod());
		response.setTransactionId(payment.getTransactionId());
		response.setStatus(payment.getStatus());

		if (payment.getStatus() == PaymentStatus.PENDING || payment.getStatus() == PaymentStatus.FAILED) {
			response.setAlternativePaymentOption("Cash payment available with confirmation");
		} else if (payment.getStatus() == PaymentStatus.CASH_REQUESTED) {
			response.setAlternativePaymentOption("Awaiting cashier confirmation");
		} else {
			response.setAlternativePaymentOption(null); // no alternative needed
		}

		return response;
	}

	public Payment toEntity(PaymentRequest request, Booking booking) {
		Payment payment = new Payment();
		payment.setBooking(booking);
		payment.setAmount(request.getAmount());
		payment.setPaymentMethod(request.getPaymentMethod());
		payment.setStatus(PaymentStatus.AWAITING_CONFIRMATION);
		return payment;
	}

//	public PaymentResponse toResponse(Payment payment) {
//		PaymentResponse response = new PaymentResponse();
//		response.setTransactionId(payment.getTransactionId());
//		response.setStatus(payment.getStatus());
//		// set other fields...
//
//		// ✅ Automatically set alternative payment option
//		if (payment.getStatus() == PaymentStatus.PENDING || payment.getStatus() == PaymentStatus.FAILED) {
//			response.setAlternativePaymentOption("Cash payment available with confirmation");
//		} else if (payment.getStatus() == PaymentStatus.CASH_REQUESTED) {
//			response.setAlternativePaymentOption("Awaiting cashier confirmation");
//		} else {
//			response.setAlternativePaymentOption(null); // no alternative needed
//		}
//
//		return response;
//	}

}
