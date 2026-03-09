package com.project.sonica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.entity.Inquiry;
import com.project.sonica.service.InquiryService;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {
	@Autowired
	private InquiryService inquiryService;

	@PostMapping
	public ResponseEntity<Inquiry> create(@RequestBody Inquiry inquiry) {
		return ResponseEntity.ok(inquiryService.createInquiry(inquiry));
	}

	@GetMapping("/status/{status}")
	public List<Inquiry> getByStatus(@PathVariable String status) {
		return inquiryService.getInquiriesByStatus(status);
	}
}
