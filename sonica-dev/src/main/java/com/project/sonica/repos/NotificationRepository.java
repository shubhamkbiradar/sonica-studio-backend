package com.project.sonica.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.sonica.entity.Customer;
import com.project.sonica.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
	List<Notification> findByCustomer(Customer customer);

	List<Notification> findByStatus(String status);
}
