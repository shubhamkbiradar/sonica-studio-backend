package com.project.sonica.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.sonica.entity.Contract;
import com.project.sonica.repos.ContractRepository;

@Component("contractSecurity")
public class ContractSecurity {
	@Autowired
	private ContractRepository contractRepository;

	public boolean isOwner(Long contractId, String username) {
		Optional<Contract> contractOpt = contractRepository.findById(contractId);

		if (contractOpt.isEmpty()) {
			return false;
		}

		Contract contract = contractOpt.get();
		if (contract.getCustomer() == null) {
			return false;
		}

		return username.equals(contract.getCustomer().getName());
	}
}
