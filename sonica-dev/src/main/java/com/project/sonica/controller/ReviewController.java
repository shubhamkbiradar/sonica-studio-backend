package com.project.sonica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.dto.ReviewRequest;
import com.project.sonica.dto.ReviewResponse;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Review;
import com.project.sonica.mapper.ReviewMapper;
import com.project.sonica.service.BookingService;
import com.project.sonica.service.CustomerService;
import com.project.sonica.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private ReviewMapper reviewMapper;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private BookingService bookingService;

	@PostMapping
	public ResponseEntity<ReviewResponse> add(@RequestBody ReviewRequest request) {
		Customer customer = customerService.getById(request.getCustomerId())
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		Booking booking = bookingService.getById(request.getBookingId())
				.orElseThrow(() -> new RuntimeException("Booking not found"));

		Review review = reviewMapper.toEntity(request, customer, booking);
		Review saved = reviewService.addReview(review);
		return ResponseEntity.ok(reviewMapper.toResponse(saved));
	}
}
