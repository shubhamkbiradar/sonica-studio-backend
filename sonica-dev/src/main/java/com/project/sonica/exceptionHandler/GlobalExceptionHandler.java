package com.project.sonica.exceptionHandler;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.project.sonica.apiResponseWrapper.ApiResponse;
import com.project.sonica.security.event.SecurityEventService;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BookingNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleBookingNotFound(BookingNotFoundException ex,
			HttpServletRequest request) {
		ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), ex.getMessage(), null);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
	}

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<ApiError> handleCustomerNotFound(CustomerNotFoundException ex, HttpServletRequest request) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Customer Not Found", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(ServicePlanNotFoundException.class)
	public ResponseEntity<ApiError> handlePlanNotFound(ServicePlanNotFoundException ex, HttpServletRequest request) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Service Plan Not Found", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(PaymentFailedException.class)
	public ResponseEntity<ApiError> handlePaymentFailed(PaymentFailedException ex, HttpServletRequest request) {
		ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Payment Failed", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.badRequest().body(error);
	}

	@ExceptionHandler(ContractNotFoundException.class)
	public ResponseEntity<ApiError> handleContractNotFound(ContractNotFoundException ex, HttpServletRequest request) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Contract Not Found", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	// Fallback for other exceptions

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ApiResponse<>(401, "Invalid username or password", null));
	}

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleUserNotFound(UsernameNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(404, ex.getMessage(), null));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApiResponse<String>> handleRuntime(RuntimeException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, ex.getMessage(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ApiResponse<>(500, "An unexpected error occurred", null));
	}

	// Logging part for exception handler
//	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
//
//	@ExceptionHandler(TokenBlacklistedException.class)
//	public ResponseEntity<ApiResponse<String>> handleBlacklistedToken(TokenBlacklistedException ex,
//			HttpServletRequest request) {
//		String ip = request.getRemoteAddr();
//		logger.warn("Blacklisted token attempt from IP={} at {}. Message={}", ip, Instant.now(), ex.getMessage());
//
//		return ResponseEntity.status(HttpStatus.FORBIDDEN)
//				.body(new ApiResponse<>(403, "Refresh token is invalidated", null));
//	}
//
//	@ExceptionHandler(TokenExpiredException.class)
//	public ResponseEntity<ApiResponse<String>> handleExpiredToken(TokenExpiredException ex,
//			HttpServletRequest request) {
//		String ip = request.getRemoteAddr();
//		logger.info("Expired token attempt from IP={} at {}. Message={}", ip, Instant.now(), ex.getMessage());
//
//		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//				.body(new ApiResponse<>(401, "Refresh token expired. Please login again.", null));
//	}
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private SecurityEventService securityEventService;

    @ExceptionHandler(TokenBlacklistedException.class)
    public ResponseEntity<ApiResponse<String>> handleBlacklistedToken(TokenBlacklistedException ex,
                                                                      HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "UNKNOWN";

        logger.warn("Blacklisted token attempt from IP={} user={} at {}", ip, username, Instant.now());
        securityEventService.logEvent("TOKEN_BLACKLISTED", username, ip, ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(403, "Refresh token is invalidated", null));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleExpiredToken(TokenExpiredException ex,
                                                                  HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "UNKNOWN";

        logger.info("Expired token attempt from IP={} user={} at {}", ip, username, Instant.now());
        securityEventService.logEvent("TOKEN_EXPIRED", username, ip, ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(401, "Refresh token expired. Please login again.", null));
    }


}
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ApiError> handleGenericException(Exception ex, HttpServletRequest request) {
//		ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
//				ex.getMessage(), request.getRequestURI());
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//	}
//
//	@ExceptionHandler(BadCredentialsException.class)
//	public ResponseEntity<ApiResponse<String>> handleBadCredentials(BadCredentialsException ex) {
//		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//				.body(new ApiResponse<>(401, "Invalid username or password", null));
//	}
//
//	@ExceptionHandler(UsernameNotFoundException.class)
//	public ResponseEntity<ApiResponse<String>> handleUserNotFound(UsernameNotFoundException ex) {
//		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(404, ex.getMessage(), null));
//	}
//
//	@ExceptionHandler(RuntimeException.class)
//	public ResponseEntity<ApiResponse<String>> handleRuntime(RuntimeException ex) {
//		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(400, ex.getMessage(), null));
//	}
//
//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex) {
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//				.body(new ApiResponse<>(500, "An unexpected error occurred", null));
//	}
