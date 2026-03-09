package com.project.sonica.exceptionHandler;

public class ContractNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContractNotFoundException(Integer contractId) {
		super("Contract not found with ID: " + contractId);
	}
}
