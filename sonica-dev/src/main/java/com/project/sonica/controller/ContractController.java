package com.project.sonica.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Contract;
import com.project.sonica.service.ContractService;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
	@Autowired
	private ContractService contractService;

	@PostMapping
	public ResponseEntity<Contract> create(@RequestBody Contract contract) {
		return ResponseEntity.ok(contractService.createContract(contract));
	}

	@GetMapping("/booking/{bookingId}")
	public ResponseEntity<Contract> getByBooking(@PathVariable Integer bookingId) {
		Booking booking = new Booking();
		booking.setBookingId(bookingId);
		return contractService.getContractByBooking(booking).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}/sign")
	public ResponseEntity<Void> sign(@PathVariable Long id) {
		contractService.signContract(id);
		return ResponseEntity.noContent().build();
	}
}
