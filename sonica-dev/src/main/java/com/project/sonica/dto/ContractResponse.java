package com.project.sonica.dto;

import java.sql.Timestamp;

public class ContractResponse {
	private long contractId;
	private Boolean signedByCustomer;
	private Timestamp signedDate;

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(long l) {
		this.contractId = l;
	}

	public Boolean getSignedByCustomer() {
		return signedByCustomer;
	}

	public void setSignedByCustomer(Boolean signedByCustomer) {
		this.signedByCustomer = signedByCustomer;
	}

	public Timestamp getSignedDate() {
		return signedDate;
	}

	public void setSignedDate(Timestamp signedDate) {
		this.signedDate = signedDate;
	}

}
