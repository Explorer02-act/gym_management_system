package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.DashboardStatsResponse;
import com.example.GymManagementSystem.dto.PendingDuesResponse;
import com.example.GymManagementSystem.repository.AttendanceRepository;
import com.example.GymManagementSystem.repository.MemberRepository;
import com.example.GymManagementSystem.repository.MembershipRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MemberService memberService;
    private final MembershipRepository membershipRepository;
    private final OfferService offerService;

    public DashboardService(MemberRepository memberRepository,
                            PaymentRepository paymentRepository,
                            AttendanceRepository attendanceRepository,
                            MemberService memberService,
                            MembershipRepository membershipRepository,
                            OfferService offerService) {
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.attendanceRepository = attendanceRepository;
        this.memberService = memberService;
        this.membershipRepository = membershipRepository;
        this.offerService = offerService;
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

        List<PendingDuesResponse> pendingMembers = membershipRepository.findByBalanceAmountGreaterThan(BigDecimal.ZERO)
                .stream()
                .map(membership -> {
                    PendingDuesResponse pending = new PendingDuesResponse();
                    pending.setMembershipId(membership.getId());
                    pending.setMemberName(membership.getMember() != null ? membership.getMember().getName() : null);
                    pending.setMemberCode(membership.getMember() != null ? membership.getMember().getMemberCode() : null);
                    pending.setPlanType(membership.getPlanType());
                    pending.setBalanceAmount(membership.getBalanceAmount());
                    pending.setPaymentStatus(membership.getPaymentStatus());
                    return pending;
                })
                .toList();

        response.setTotalPendingCollections(pendingMembers.stream()
                .map(PendingDuesResponse::getBalanceAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setMembersWithPendingDues(pendingMembers);
        response.setActiveOffers(offerService.getActiveOffers());
        return response;
    }
}
