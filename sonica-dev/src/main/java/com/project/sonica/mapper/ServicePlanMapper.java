package com.project.sonica.mapper;

import org.springframework.stereotype.Component;

import com.project.sonica.dto.ServicePlanRequest;
import com.project.sonica.dto.ServicePlanResponse;
import com.project.sonica.entity.ServicePlan;

@Component
public class ServicePlanMapper {
	public ServicePlanResponse toResponse(ServicePlan plan) {
		ServicePlanResponse dto = new ServicePlanResponse();
		dto.setPlanId(plan.getPlanId());
		dto.setName(plan.getName());
		dto.setDescription(plan.getDescription());
		dto.setPrice(plan.getPrice());
		dto.setEventType(plan.getEventType());
		dto.setValidityPeriod(plan.getValidityPeriod());
		return dto;
	}

	public ServicePlan toEntity(ServicePlanRequest request) {
		ServicePlan plan = new ServicePlan();
		plan.setName(request.getName());
		plan.setDescription(request.getDescription());
		plan.setPrice(request.getPrice());
		plan.setEventType(request.getEventType());
		plan.setValidityPeriod(request.getValidityPeriod());
		return plan;
	}
}
