package com.project.sonica.entity;


import java.sql.Timestamp;

import com.project.sonica.security.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Contract")
public class Contract {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long contractId;

	@OneToOne
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking booking;
	
	@OneToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	private String terms;

	public long getContractId() {
		return contractId;
	}

	public void setContractId(long l) {
		this.contractId = l;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public String getTerms() {
		return terms;
	}

	public void setTerms(String terms) {
		this.terms = terms;
	}

	public Boolean getSignedByCustomer() {
		return signedByCustomer;
	}

	public void setSignedByCustomer(Boolean signedByCustomer) {
		this.signedByCustomer = signedByCustomer;
	}

	public Timestamp getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(Timestamp signedDate) {
		this.signedDate = signedDate;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	private Boolean signedByCustomer;
	private Timestamp signedDate;
	private Timestamp createdAt;
}
