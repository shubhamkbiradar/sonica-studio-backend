package com.project.sonica.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.dto.BookingRequest;
import com.project.sonica.dto.BookingResponse;
import com.project.sonica.apiResponseWrapper.ApiResponse;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.ServicePlan;
import com.project.sonica.exceptionHandler.CustomerNotFoundException;
import com.project.sonica.exceptionHandler.ServicePlanNotFoundException;
import com.project.sonica.mapper.BookingMapper;
import com.project.sonica.pagedResponse.PagedResponse;
import com.project.sonica.responseBuilderUtility.ResponseBuilder;
import com.project.sonica.service.BookingService;
import com.project.sonica.service.CustomerService;
import com.project.sonica.service.ServicePlanService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
	@Autowired
	private BookingService bookingService;
	@Autowired
	private BookingMapper bookingMapper;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ServicePlanService planService;
	@Autowired
	private ResponseBuilder responseBuilder;

	@PostMapping
	public ResponseEntity<ApiResponse<BookingResponse>> create(@RequestBody BookingRequest request) {
	    Customer customer = customerService.getById(request.getCustomerId())
	            .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));
	    ServicePlan plan = planService.getPlanById(request.getPlanId())
	            .orElseThrow(() -> new ServicePlanNotFoundException(request.getPlanId()));

	    Booking booking = bookingMapper.toEntity(request, customer, plan);
	    Booking saved = bookingService.createBooking(booking);
	    BookingResponse response = bookingMapper.toResponse(saved);

	    ApiResponse<BookingResponse> apiResponse =
	            new ApiResponse<>(HttpStatus.OK.value(), "Booking created successfully", response);

	    return ResponseEntity.ok(apiResponse);
	}

	@GetMapping("/customer/{customerId}")
	public List<BookingResponse> getByCustomer(@PathVariable Integer customerId) {
		Customer customer = customerService.getById(customerId)
				.orElseThrow(() -> new RuntimeException("Customer not found"));
		return bookingService.getBookingsByCustomer(customer).stream().map(bookingMapper::toResponse)
				.collect(Collectors.toList());
	}
	
	@GetMapping
	public ResponseEntity<PagedResponse<BookingResponse>> getAllBookings(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
		ResponseBuilder responseBuilder = new ResponseBuilder();

	    Page<Booking> bookingPage = bookingService.getAllBookings(PageRequest.of(page, size));
	    Page<BookingResponse> dtoPage = bookingPage.map(bookingMapper::toResponse);

	    return responseBuilder.paged(dtoPage, "Bookings fetched successfully");
	}
	
	@GetMapping("/search")
	public ResponseEntity<PagedResponse<BookingResponse>> searchBookings(
	        @RequestParam Map<String, String> filters,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "eventDate") String sortBy,
	        @RequestParam(defaultValue = "asc") String direction) {

	    Pageable pageable = PageRequest.of(page, size,
	            direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

	    Page<Booking> bookingPage = bookingService.getFilteredBookings(filters, pageable);
	    Page<BookingResponse> dtoPage = bookingPage.map(bookingMapper::toResponse);

	    return responseBuilder.paged(dtoPage, "Filtered bookings fetched successfully");
	}

}
