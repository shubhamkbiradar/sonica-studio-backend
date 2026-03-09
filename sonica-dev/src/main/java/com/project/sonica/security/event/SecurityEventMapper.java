package com.project.sonica.security.event;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class SecurityEventMapper {
	public SecurityEventDTO toDTO(SecurityEvent event) {
		return new SecurityEventDTO(event.getId(), event.getEventType(), event.getUsername(), event.getIpAddress(),
				event.getDetails(), event.getTimestamp());
	}

	public List<SecurityEventDTO> toDTOList(List<SecurityEvent> events) {
		return events.stream().map(this::toDTO).collect(Collectors.toList());
	}
}
