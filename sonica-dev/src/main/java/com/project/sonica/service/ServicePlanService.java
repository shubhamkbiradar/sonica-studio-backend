package com.project.sonica.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.sonica.entity.ServicePlan;
import com.project.sonica.genericSearch.SearchOperation;
import com.project.sonica.genericSearch.SpecificationBuilder;
import com.project.sonica.repos.ServicePlanRepository;

@Service
public class ServicePlanService {
	private final ServicePlanRepository servicePlanRepository;

	public ServicePlanService(ServicePlanRepository servicePlanRepository) {
		this.servicePlanRepository = servicePlanRepository;
	}

	@PreAuthorize("hasRole('ADMIN')")
	public ServicePlan createPlan(ServicePlan plan) {
		return servicePlanRepository.save(plan);
	}

	@PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
	public List<ServicePlan> getAllPlans() {
		return servicePlanRepository.findAll();
	}

	public Optional<ServicePlan> getPlanById(Integer planId) {
		return servicePlanRepository.findById(planId);
	}

	public List<ServicePlan> getPlansByEventType(String eventType) {
		return servicePlanRepository.findByEventType(eventType);
	}

	public ServicePlan updatePlan(Integer planId, ServicePlan updatedPlan) {
		ServicePlan plan = servicePlanRepository.findById(planId)
				.orElseThrow(() -> new RuntimeException("Service Plan not found"));
		plan.setName(updatedPlan.getName());
		plan.setDescription(updatedPlan.getDescription());
		plan.setPrice(updatedPlan.getPrice());
		plan.setEventType(updatedPlan.getEventType());
		plan.setValidityPeriod(updatedPlan.getValidityPeriod());
		return servicePlanRepository.save(plan);
	}

	public Page<ServicePlan> getFilteredPlans(Map<String, String> filters, Pageable pageable) {
		SpecificationBuilder<ServicePlan> builder = new SpecificationBuilder<>();

		if (filters.containsKey("eventType")) {
			builder.with("eventType", filters.get("eventType"), SearchOperation.EQUAL);
		}
		if (filters.containsKey("minPrice") && filters.containsKey("maxPrice")) {
			BigDecimal min = new BigDecimal(filters.get("minPrice"));
			BigDecimal max = new BigDecimal(filters.get("maxPrice"));
			builder.with("price", Arrays.asList(min, max), SearchOperation.BETWEEN);
		}

		Specification<ServicePlan> spec = builder.build();
		return servicePlanRepository.findAll(spec, pageable);
	}

	@PreAuthorize("hasRole('ADMIN')")
	public void deletePlan(Integer planId) {
		servicePlanRepository.deleteById(planId);
	}
}
