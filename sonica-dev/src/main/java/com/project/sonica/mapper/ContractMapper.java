package com.project.sonica.mapper;

import org.springframework.stereotype.Component;

import com.project.sonica.dto.ContractRequest;
import com.project.sonica.dto.ContractResponse;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.Contract;

@Component
public class ContractMapper {
	public ContractResponse toResponse(Contract contract) {
		ContractResponse dto = new ContractResponse();
		dto.setContractId(contract.getContractId());
		dto.setSignedByCustomer(contract.getSignedByCustomer());
		dto.setSignedDate(contract.getSignedDate());
		return dto;
	}

	public Contract toEntity(ContractRequest request, Booking booking) {
		Contract contract = new Contract();
		contract.setBooking(booking);
		contract.setTerms(request.getTerms());
		contract.setSignedByCustomer(false);
		return contract;
	}
}
