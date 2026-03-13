package com.project.sonica.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.sonica.aop.annotations.RequireAnyRole;
import com.project.sonica.aop.annotations.RequireRole;
import com.project.sonica.aop.annotations.SonicaTx;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.BookingStatus;
import com.project.sonica.entity.Customer;
import com.project.sonica.genericSearch.SearchOperation;
import com.project.sonica.genericSearch.SpecificationBuilder;
import com.project.sonica.repos.BookingRepository;

@Service
public class BookingService {
	private final BookingRepository bookingRepository;

	public BookingService(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	@RequireRole("CUSTOMER")
	@SonicaTx
	public Booking createBooking(Booking booking) {
		return bookingRepository.save(booking);
	}

	
	public List<Booking> getBookingsByCustomer(Customer customer) {
		return bookingRepository.findByCustomer(customer);
	}

	public List<Booking> getBookingsByDate(LocalDate date) {
		return bookingRepository.findByEventDate(date);
	}

	@PreAuthorize("hasRole('ADMIN') or #booking.customer.username == authentication.name")
	@SonicaTx
	public Booking updateBooking(Booking booking, Integer bookingId) {
		Booking existing = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new RuntimeException("Booking not found"));
		existing.setEventDate(booking.getEventDate());
		existing.setStatus(booking.getStatus());
		return bookingRepository.save(existing);
	}

	public Optional<Booking> getById(Integer id) {
		return bookingRepository.findById(id);
	}

	public Page<Booking> getAllBookings(Pageable pageable) {
		return bookingRepository.findAll(pageable);
	}

	public Page<Booking> getFilteredBookings(Map<String, String> filters,
			org.springframework.data.domain.Pageable pageable) {
		SpecificationBuilder<Booking> builder = new SpecificationBuilder<>();

		if (filters.containsKey("status")) {
			builder.with("status", filters.get("status"), SearchOperation.EQUAL);
		}
		if (filters.containsKey("location")) {
			builder.with("location", filters.get("location"), SearchOperation.LIKE);
		}
		if (filters.containsKey("startDate") && filters.containsKey("endDate")) {
			LocalDate start = LocalDate.parse(filters.get("startDate"));
			LocalDate end = LocalDate.parse(filters.get("endDate"));
			builder.with("eventDate", Arrays.asList(start, end), SearchOperation.BETWEEN);
		}

		Specification<Booking> spec = builder.build();
		return bookingRepository.findAll(spec, pageable);
	}

	@RequireAnyRole({ "ADMIN", "CUSTOMER" })
	public List<Booking> getBookings() {
		return bookingRepository.findAll();
	}

}
