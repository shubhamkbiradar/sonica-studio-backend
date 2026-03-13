package com.project.sonica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.sonica.aop.annotations.SonicaTx;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Notification;
import com.project.sonica.repos.NotificationRepository;

@Service
public class NotificationService {
	private final NotificationRepository notificationRepository;

	public NotificationService(NotificationRepository notificationRepository) {
		this.notificationRepository = notificationRepository;
	}

	@SonicaTx
	public Notification sendNotification(Notification notification) {
		// Integrate WhatsApp/Email API here
		return notificationRepository.save(notification);
	}

	public List<Notification> getNotificationsByCustomer(Customer customer) {
		return notificationRepository.findByCustomer(customer);
	}
}
