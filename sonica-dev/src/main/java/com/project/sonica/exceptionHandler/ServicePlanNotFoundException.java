package com.project.sonica.exceptionHandler;

public class ServicePlanNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServicePlanNotFoundException(Integer planId) {
        super("Service Plan not found with ID: " + planId);
    }
}
