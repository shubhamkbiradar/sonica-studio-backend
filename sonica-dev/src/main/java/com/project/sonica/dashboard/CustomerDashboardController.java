package com.project.sonica.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.dto.CustomerDashboard;
import com.project.sonica.service.DashboardService;

@RestController
@RequestMapping("/customer")
public class CustomerDashboardController {
    private final DashboardService dashboardService;

    public CustomerDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/{name}")
    public ResponseEntity<CustomerDashboard> dashboard(@PathVariable String name) {
        return ResponseEntity.ok(dashboardService.getCustomerDashboard(name));
    }
}
