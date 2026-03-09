package com.project.sonica.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.project.sonica.dto.BookingRequest;
import com.project.sonica.dto.BookingResponse;
import com.project.sonica.entity.Booking;
import com.project.sonica.entity.BookingStatus;
import com.project.sonica.entity.Customer;
import com.project.sonica.entity.ServicePlan;

@Component
public class BookingMapper {
    @Autowired
    private ServicePlanMapper planMapper;

    public BookingResponse toResponse(Booking booking) {
        BookingResponse dto = new BookingResponse();
        dto.setBookingId(booking.getBookingId());
        dto.setEventType(booking.getEventType());
        dto.setEventDate(booking.getEventDate());
        dto.setLocation(booking.getLocation());
        dto.setStatus(booking.getStatus());
        if (booking.getPlan() != null) {
            dto.setPlan(planMapper.toResponse(booking.getPlan()));
        }
        return dto;
    }

    public Booking toEntity(BookingRequest request, Customer customer, ServicePlan plan) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setEventType(request.getEventType());
        booking.setEventDate(request.getEventDate());
        booking.setLocation(request.getLocation());
        booking.setPlan(plan);
        booking.setStatus(BookingStatus.PENDING_CONFIRMATION);
        return booking;
    }
}
