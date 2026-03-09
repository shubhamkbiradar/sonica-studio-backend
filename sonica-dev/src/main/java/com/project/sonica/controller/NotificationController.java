package com.project.sonica.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Notification;
import com.project.sonica.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
	@Autowired
	private NotificationService notificationService;

	@PostMapping
	public ResponseEntity<Notification> send(@RequestBody Notification notification) {
		return ResponseEntity.ok(notificationService.sendNotification(notification));
	}

	@GetMapping("/customer/{customerId}")
	public List<Notification> getByCustomer(@PathVariable Integer customerId) {
		Customer customer = new Customer();
		customer.setCustomerId(customerId);
		return notificationService.getNotificationsByCustomer(customer);
	}
}
