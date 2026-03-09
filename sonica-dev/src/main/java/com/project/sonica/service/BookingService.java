package com.project.sonica.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.BookingStatus;
import com.project.sonica.entity.Customer;
import com.project.sonica.genericSearch.SearchOperation;
import com.project.sonica.genericSearch.SpecificationBuilder;
import com.project.sonica.photographyServices.PhotographyServiceRepository;
import com.project.sonica.photographyServices.PhotographyServices;
import com.project.sonica.repos.BookingRepository;
import com.project.sonica.security.User;
import com.project.sonica.security.UserRepository;

@Service
public class BookingService {
	@Autowired
	private final BookingRepository bookingRepository;
	@Autowired
	private final UserRepository userRepository;
	@Autowired
	private final PhotographyServiceRepository serviceRepository;

	public BookingService(BookingRepository bookingRepository, UserRepository userRepository,
			PhotographyServiceRepository serviceRepository) {
		this.bookingRepository = bookingRepository;
		this.userRepository = userRepository;
		this.serviceRepository = serviceRepository;
	}


	@PreAuthorize("hasRole('CUSTOMER')")
	public Booking createBooking(Booking booking) {
		return bookingRepository.save(booking);
	}

	//customer can book
	// Customer books a service
    public Booking bookService(Long customerId, Long serviceId) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        PhotographyServices service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        Booking booking = new Booking();
//        booking.setCustomer(customer);
        booking.setCustomerName(customer.getPassword());
        booking.setPhotographyServices(service);
        booking.setDate(LocalDate.now().toString());
        booking.setStatus(BookingStatus.PENDING_CONFIRMATION);

        return bookingRepository.save(booking);
    }

    // Customer views their bookings
//    public List<Booking> getBookingsByCustomer(Long customerId) {
//        User customer = userRepository.findById(customerId)
//                .orElseThrow(() -> new RuntimeException("Customer not found"));
//        return bookingRepository.findByCustomer(customer);
//    }

	
	public List<Booking> getBookingsByCustomer(Customer customer) {
		return bookingRepository.findByCustomer(customer);
	}

	public List<Booking> getBookingsByDate(LocalDate date) {
		return bookingRepository.findByEventDate(date);
	}

	@PreAuthorize("hasRole('ADMIN') or #booking.customer.username == authentication.name")
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

	public Page<Booking> getAllBookings(PageRequest pageRequest) {
		return bookingRepository.findAll(pageRequest);
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

	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	public List<Booking> getBookings() {
		return bookingRepository.findAll();
	}

}
