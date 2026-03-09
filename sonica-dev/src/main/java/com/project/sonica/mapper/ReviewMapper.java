package com.project.sonica.mapper;

import org.springframework.stereotype.Component;

import com.project.sonica.dto.ReviewRequest;
import com.project.sonica.dto.ReviewResponse;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Review;

@Component
public class ReviewMapper {
	public ReviewResponse toResponse(Review review) {
		ReviewResponse dto = new ReviewResponse();
		dto.setReviewId(review.getReviewId());
		dto.setRating(review.getRating());
		dto.setFeedback(review.getFeedback());
		dto.setCustomerName(review.getCustomer().getName());
		return dto;
	}

	public Review toEntity(ReviewRequest request, Customer customer, Booking booking) {
		Review review = new Review();
		review.setCustomer(customer);
		review.setBooking(booking);
		review.setRating(request.getRating());
		review.setFeedback(request.getFeedback());
		return review;
	}
}
