package com.project.sonica.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
	List<Review> findByCustomer(Customer customer);

	List<Review> findByBooking(Booking booking);
}
