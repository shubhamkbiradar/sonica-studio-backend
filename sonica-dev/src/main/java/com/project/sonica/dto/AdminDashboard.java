package com.project.sonica.dto;

import java.util.List;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Inquiry;
import com.project.sonica.entity.Payment;
import com.project.sonica.entity.Review;

public class AdminDashboard {

	private List<Booking> bookings;
	private List<Payment> payments;
	private List<Inquiry> inquiries;
	private List<Review> reviews;

	// Constructors
	public AdminDashboard() {
	}

	public AdminDashboard(List<Booking> bookings, List<Payment> payments, List<Inquiry> inquiries,
			List<Review> reviews) {
		this.bookings = bookings;
		this.payments = payments;
		this.inquiries = inquiries;
		this.reviews = reviews;
	}

	// Getters and Setters
	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

	public List<Payment> getPayments() {
		return payments;
	}

	public void setPayments(List<Payment> payments) {
		this.payments = payments;
	}

	public List<Inquiry> getInquiries() {
		return inquiries;
	}

	public void setInquiries(List<Inquiry> inquiries) {
		this.inquiries = inquiries;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
}
