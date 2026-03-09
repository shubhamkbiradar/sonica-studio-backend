package com.project.sonica.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.dto.AdminDashboard;
import com.project.sonica.service.DashboardService;

@RestController
@RequestMapping("/admin")
public class AdminDashboardController {
	private final DashboardService dashboardService;

	public AdminDashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/dashboard")
	public ResponseEntity<AdminDashboard> dashboard() {
		return ResponseEntity.ok(dashboardService.getAdminDashboard());
	}
}
