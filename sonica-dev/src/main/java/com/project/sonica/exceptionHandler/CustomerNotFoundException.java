package com.project.sonica.exceptionHandler;

public class CustomerNotFoundException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomerNotFoundException(Integer customerId) {
		super("Customer not found with ID: " + customerId);
	}
}
