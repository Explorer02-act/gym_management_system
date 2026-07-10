package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.DashboardStatsResponse;
import com.example.GymManagementSystem.repository.AttendanceRepository;
import com.example.GymManagementSystem.repository.MemberRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
public class DashboardService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MemberService memberService;

    public DashboardService(MemberRepository memberRepository,
                            PaymentRepository paymentRepository,
                            AttendanceRepository attendanceRepository,
                            MemberService memberService) {
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.attendanceRepository = attendanceRepository;
        this.memberService = memberService;
    }

    public DashboardStatsResponse getStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate yearStart = today.withDayOfYear(1);

        DashboardStatsResponse response = new DashboardStatsResponse();
        response.setTotalMembers(memberRepository.count());
        response.setActiveMembers(memberService.countActiveMembers());
        response.setExpiredMembers(memberService.countExpiredMembers());
        response.setMembershipsExpiringSoon(memberService.countExpiringSoonMembers());
        response.setTodaysCheckIns(attendanceRepository.countByAttendanceDate(today));
        response.setTodaysRevenue(paymentRepository.sumByPaymentDate(today));
        response.setWeeklyRevenue(paymentRepository.sumBetween(weekStart, today));
        response.setMonthlyRevenue(paymentRepository.sumBetween(monthStart, today));
        response.setYearlyRevenue(paymentRepository.sumBetween(yearStart, today));
        response.setLifetimeRevenue(paymentRepository.sumLifetime());
        return response;
    }
}
