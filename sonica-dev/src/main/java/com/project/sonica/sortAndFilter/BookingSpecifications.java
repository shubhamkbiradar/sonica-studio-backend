package com.project.sonica.sortAndFilter;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.project.sonica.entity.Booking;

public class BookingSpecifications {
	public static Specification<Booking> hasStatus(String status) {
		return (root, query, cb) -> cb.equal(root.get("status"), status);
	}

	public static Specification<Booking> hasEventDate(LocalDate date) {
		return (root, query, cb) -> cb.equal(root.get("eventDate"), date);
	}

	public static Specification<Booking> hasLocation(String location) {
		return (root, query, cb) -> cb.like(root.get("location"), "%" + location + "%");
	}
}
