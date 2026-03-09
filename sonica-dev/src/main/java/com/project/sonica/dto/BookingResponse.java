package com.project.sonica.dto;

import java.time.LocalDate;

import com.project.sonica.entity.BookingStatus;

public class BookingResponse {
	private Integer bookingId;
	private String eventType;
	private LocalDate eventDate;
	private String location;
	private BookingStatus status;
	private ServicePlanResponse plan;

	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public LocalDate getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus bookingStatus) {
		this.status = bookingStatus;
	}

	public ServicePlanResponse getPlan() {
		return plan;
	}

	public void setPlan(ServicePlanResponse plan) {
		this.plan = plan;
	}

}
