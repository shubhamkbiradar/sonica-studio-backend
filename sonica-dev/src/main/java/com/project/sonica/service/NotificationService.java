package com.project.sonica.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Notification;
import com.project.sonica.repos.NotificationRepository;

@Service
public class NotificationService {
	@Autowired
	private NotificationRepository notificationRepository;

	public Notification sendNotification(Notification notification) {
		// Integrate WhatsApp/Email API here
		return notificationRepository.save(notification);
	}

	public List<Notification> getNotificationsByCustomer(Customer customer) {
		return notificationRepository.findByCustomer(customer);
	}
}
