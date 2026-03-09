package com.project.sonica.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Contract;
import com.project.sonica.repos.ContractRepository;

@Service
public class ContractService {
	@Autowired
	private ContractRepository contractRepository;

	public Contract createContract(Contract contract) {
		return contractRepository.save(contract);
	}

	public Optional<Contract> getContractByBooking(Booking booking) {
		return contractRepository.findByBooking(booking);
	}

	@PreAuthorize("hasRole('ADMIN') or @contractSecurity.isOwner(#contractId, authentication.name)")
	public Contract getContract(Long contractId) {
		return contractRepository.findById(contractId).orElseThrow(() -> new RuntimeException("Contract not found"));
	}

	public void signContract(Long contractId) {
		Contract contract = contractRepository.findById(contractId)
				.orElseThrow(() -> new RuntimeException("Contract not found"));
		contract.setSignedByCustomer(true);
		contract.setSignedDate(new Timestamp(System.currentTimeMillis()));
		contractRepository.save(contract);
	}
}
