package com.project.sonica.dto;

import java.math.BigDecimal;

import com.project.sonica.entity.PaymentStatus;

public class PaymentResponse {
	private Integer paymentId;
	private BigDecimal amount;
	private String paymentMethod;
	private String transactionId;
	private PaymentStatus status;
	private String alternativePaymentOption;

	public String getAlternativePaymentOption() {
		return alternativePaymentOption;
	}

	public void setAlternativePaymentOption(String alternativePaymentOption) {
		this.alternativePaymentOption = alternativePaymentOption;
	}

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus paymentStatus) {
		this.status = paymentStatus;
	}

}
