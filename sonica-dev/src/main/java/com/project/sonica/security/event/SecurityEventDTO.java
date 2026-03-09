package com.project.sonica.security.event;

import java.time.Instant;

public class SecurityEventDTO {
	private Long id;
	private String type;
	private String username;
	private String ip;
	private String message;
	private Instant timestamp;

	// constructor
	public SecurityEventDTO(Long id, String type, String username, String ip, String message, Instant timestamp) {
		this.id = id;
		this.type = type;
		this.username = username;
		this.ip = ip;
		this.message = message;
		this.timestamp = timestamp;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public Long getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getUsername() {
		return username;
	}

	public String getIp() {
		return ip;
	}

	public String getMessage() {
		return message;
	}

	public Instant getTimestamp() {
		return timestamp;
	}
}
