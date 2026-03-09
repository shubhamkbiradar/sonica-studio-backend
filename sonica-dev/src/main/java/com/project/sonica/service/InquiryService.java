package com.project.sonica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.sonica.entity.Inquiry;
import com.project.sonica.repos.InquiryRepository;

@Service
public class InquiryService {
	@Autowired
	private InquiryRepository inquiryRepository;

	public Inquiry createInquiry(Inquiry inquiry) {
		return inquiryRepository.save(inquiry);
	}

	public List<Inquiry> getInquiriesByStatus(String status) {
		return inquiryRepository.findByStatus(status);
	}
}
