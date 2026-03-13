package com.project.sonica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.sonica.aop.annotations.SonicaTx;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Review;
import com.project.sonica.repos.ReviewRepository;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @SonicaTx
    public Review addReview(Review review) {
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsByBooking(Booking booking) {
        return reviewRepository.findByBooking(booking);
    }

    public List<Review> getReviewsByCustomer(Customer customer) {
        return reviewRepository.findByCustomer(customer);
    }
}

