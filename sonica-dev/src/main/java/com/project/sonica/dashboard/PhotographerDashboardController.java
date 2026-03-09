package com.project.sonica.dashboard;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photographer")
public class PhotographerDashboardController {
	private final PhotographerDashboardService dashboardService;

	public PhotographerDashboardController(PhotographerDashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/dashboard/{name}")
	public ResponseEntity<Map<String, Object>> dashboard(@PathVariable String name) {
		return ResponseEntity.ok(dashboardService.getPhotographerData(name));
	}
}
