package com.project.sonica.security.event;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.aop.annotations.RequireRole;
import com.project.sonica.aop.annotations.SonicaTx;
import com.project.sonica.apiResponseWrapper.ApiResponse;

@RestController
@RequestMapping("/api/admin/security-events")
@RequireRole("ADMIN")
public class SecurityEventController {
	@Autowired
	private SecurityEventRepository securityEventRepository;
	@Autowired
	private SecurityEventMapper mapper;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<SecurityEvent>>> getAllEvents() {
		List<SecurityEvent> events = securityEventRepository.findAll();
		return ResponseEntity.ok(new ApiResponse<>(200, "Security events fetched successfully", events));
	}

	@GetMapping("/user/{username}")
	public ResponseEntity<ApiResponse<List<SecurityEvent>>> getEventsByUser(@PathVariable String username) {
		List<SecurityEvent> events = securityEventRepository.findByUsername(username, Pageable.unpaged()).getContent();
		return ResponseEntity.ok(new ApiResponse<>(200, "Security events for user fetched successfully", events));
	}

	@DeleteMapping("/{id}")
	@SonicaTx
	public ResponseEntity<ApiResponse<String>> deleteEvent(@PathVariable Long id) {
		securityEventRepository.deleteById(id);
		return ResponseEntity.ok(new ApiResponse<>(200, "Security event deleted successfully", "Deleted ID: " + id));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Map<String, Object>>> getEvents(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(required = false) String type,
			@RequestParam(required = false) String username,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
		Page<SecurityEvent> eventPage;

		if (start != null && end != null) {
			eventPage = securityEventRepository.findByTimestampBetween(start, end, pageable);
		} else if (type != null) {
			eventPage = securityEventRepository.findByEventType(type, pageable);
		} else if (username != null) {
			eventPage = securityEventRepository.findByUsername(username, pageable);
		} else {
			eventPage = securityEventRepository.findAll(pageable);
		}

		Map<String, Object> responseData = new HashMap<>();
		responseData.put("events", mapper.toDTOList(eventPage.getContent()));
		responseData.put("currentPage", eventPage.getNumber());
		responseData.put("totalItems", eventPage.getTotalElements());
		responseData.put("totalPages", eventPage.getTotalPages());

		return ResponseEntity.ok(new ApiResponse<>(200, "Security events fetched successfully", responseData));
	}

}
