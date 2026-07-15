package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.RevenueByPlanResponse;
import com.example.GymManagementSystem.dto.RevenueSummaryResponse;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class RevenueAnalyticsService {

    private final PaymentRepository paymentRepository;

    public RevenueAnalyticsService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public RevenueSummaryResponse getSummary() {
        LocalDate today = LocalDate.now();
        RevenueSummaryResponse response = new RevenueSummaryResponse();
        response.setTodaysRevenue(paymentRepository.sumByPaymentDate(today));
        response.setWeeklyRevenue(paymentRepository.sumBetween(today.with(DayOfWeek.MONDAY), today));
        response.setMonthlyRevenue(paymentRepository.sumBetween(today.withDayOfMonth(1), today));
        response.setYearlyRevenue(paymentRepository.sumBetween(today.withDayOfYear(1), today));
        response.setLifetimeRevenue(paymentRepository.sumLifetime());
        response.setTodaysCashRevenue(paymentRepository.sumCashByPaymentDate(today));
        response.setTodaysUpiRevenue(paymentRepository.sumUpiByPaymentDate(today));
        response.setMonthlyCashRevenue(paymentRepository.sumCashBetween(today.withDayOfMonth(1), today));
        response.setMonthlyUpiRevenue(paymentRepository.sumUpiBetween(today.withDayOfMonth(1), today));
        response.setRevenueByPlan(getRevenueByPlan());
        return response;
    }

    public List<RevenueByPlanResponse> getRevenueByPlan() {
        return paymentRepository.revenueByPlan()
                .stream()
                .map(row -> {
                    RevenueByPlanResponse response = new RevenueByPlanResponse();
                    response.setPlanName(row.getPlanName());
                    response.setTotalRevenue(row.getTotalRevenue());
                    response.setPaymentCount(row.getPaymentCount() == null ? 0 : row.getPaymentCount());
                    return response;
                })
                .toList();
    }
}
