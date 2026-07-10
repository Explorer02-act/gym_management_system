package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.PaymentResponse;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentResponse> getPayments(String filter) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end = today;

        String normalized = filter == null ? "month" : filter.toLowerCase();
        switch (normalized) {
            case "today" -> start = today;
            case "week" -> start = today.with(DayOfWeek.MONDAY);
            case "month" -> start = today.withDayOfMonth(1);
            case "year" -> start = today.withDayOfYear(1);
            default -> throw new BadRequestException("Unsupported payment filter: " + filter);
        }

        return paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDescIdDesc(start, end)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
