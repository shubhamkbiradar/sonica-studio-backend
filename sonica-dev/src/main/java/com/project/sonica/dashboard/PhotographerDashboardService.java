package com.project.sonica.dashboard;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.sonica.entity.PaymentStatus;
import com.project.sonica.repos.BookingRepository;
import com.project.sonica.repos.PaymentRepository;

@Service
public class PhotographerDashboardService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    public PhotographerDashboardService(BookingRepository bookingRepository,
                            PaymentRepository paymentRepository) {
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    // Photographer dashboard data
    public Map<String, Object> getPhotographerData(String photographerName) {
        Map<String, Object> data = new HashMap<>();
        data.put("upcomingShoots", bookingRepository.findByPhotographerName(photographerName));
        data.put("pendingApprovals", paymentRepository.countByStatus(PaymentStatus.PENDING));
        return data;
    }
}
