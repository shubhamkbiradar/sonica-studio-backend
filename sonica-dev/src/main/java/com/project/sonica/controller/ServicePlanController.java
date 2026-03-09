package com.project.sonica.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.dto.ServicePlanRequest;
import com.project.sonica.dto.ServicePlanResponse;
import com.project.sonica.entity.ServicePlan;
import com.project.sonica.mapper.ServicePlanMapper;
import com.project.sonica.pagedResponse.PagedResponse;
import com.project.sonica.responseBuilderUtility.ResponseBuilder;
import com.project.sonica.service.ServicePlanService;

@RestController
@RequestMapping("/api/plans")
public class ServicePlanController {
	private final ServicePlanService servicePlanService;
	private final ServicePlanMapper planMapper;
	private final ResponseBuilder responseBuilder;

	public ServicePlanController(ServicePlanService servicePlanService, ServicePlanMapper planMapper,
			ResponseBuilder responseBuilder) {
		this.servicePlanService = servicePlanService;
		this.planMapper = planMapper;
		this.responseBuilder = responseBuilder;
	}

	@PostMapping
	public ResponseEntity<ServicePlanResponse> create(@RequestBody ServicePlanRequest request) {
		ServicePlan plan = planMapper.toEntity(request);
		ServicePlan saved = servicePlanService.createPlan(plan);
		return ResponseEntity.ok(planMapper.toResponse(saved));
	}

	@GetMapping
	public List<ServicePlanResponse> getAll() {
		return servicePlanService.getAllPlans().stream().map(planMapper::toResponse).collect(Collectors.toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ServicePlanResponse> getById(@PathVariable Integer id) {
		return servicePlanService.getPlanById(id).map(planMapper::toResponse).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/plans/search")
	public ResponseEntity<PagedResponse<ServicePlanResponse>> searchPlans(
	        @RequestParam Map<String, String> filters,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(defaultValue = "price") String sortBy,
	        @RequestParam(defaultValue = "asc") String direction) {
		
	    Pageable pageable = PageRequest.of(page, size,
	            direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

	    Page<ServicePlan> planPage = servicePlanService.getFilteredPlans(filters, pageable);
	    Page<ServicePlanResponse> dtoPage = planPage.map(planMapper::toResponse);

	    return responseBuilder.paged(dtoPage, "Filtered service plans fetched successfully");
	}
}
