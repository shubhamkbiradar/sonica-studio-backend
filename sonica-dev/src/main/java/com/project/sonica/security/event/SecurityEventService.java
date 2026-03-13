package com.project.sonica.security.event;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.sonica.aop.annotations.SonicaTx;

@Service
public class SecurityEventService {
	@Autowired
	private SecurityEventRepository securityEventRepository;

	@SonicaTx
	public void logEvent(String eventType, String username, String ipAddress, String details) {
		SecurityEvent event = new SecurityEvent();
		event.setEventType(eventType);
		event.setUsername(username);
		event.setIpAddress(ipAddress);
		event.setDetails(details);
		event.setTimestamp(Instant.now());
		securityEventRepository.save(event);
	}
}
