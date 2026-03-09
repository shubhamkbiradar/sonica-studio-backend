package com.project.sonica.mapper;

import org.springframework.stereotype.Component;

import com.project.sonica.dto.NotificationRequest;
import com.project.sonica.dto.NotificationResponse;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Notification;

@Component
public class NotificationMapper {
	public NotificationResponse toResponse(Notification notification) {
		NotificationResponse dto = new NotificationResponse();
		dto.setNotificationId(notification.getNotificationId());
		dto.setType(notification.getType());
		dto.setMessage(notification.getMessage());
		dto.setStatus(notification.getStatus());
		return dto;
	}

	public Notification toEntity(NotificationRequest request, Customer customer) {
		Notification notification = new Notification();
		notification.setCustomer(customer);
		notification.setType(request.getType());
		notification.setMessage(request.getMessage());
		notification.setStatus("Sent");
		return notification;
	}
}
