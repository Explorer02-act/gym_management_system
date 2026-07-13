package com.example.GymManagementSystem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MemberProfileResponse {

    private MemberResponse member;
    private MembershipResponse currentMembership;
    private List<MembershipResponse> membershipHistory;
    private List<MembershipPauseResponse> pauseHistory;
    private List<AttendanceResponse> attendanceHistory;
    private long totalVisits;
    private BigDecimal revenueGenerated;
    private LocalDate upcomingExpiry;

    public MemberResponse getMember() { return member; }
    public void setMember(MemberResponse member) { this.member = member; }
    public MembershipResponse getCurrentMembership() { return currentMembership; }
    public void setCurrentMembership(MembershipResponse currentMembership) { this.currentMembership = currentMembership; }
    public List<MembershipResponse> getMembershipHistory() { return membershipHistory; }
    public void setMembershipHistory(List<MembershipResponse> membershipHistory) { this.membershipHistory = membershipHistory; }
    public List<MembershipPauseResponse> getPauseHistory() { return pauseHistory; }
    public void setPauseHistory(List<MembershipPauseResponse> pauseHistory) { this.pauseHistory = pauseHistory; }
    public List<AttendanceResponse> getAttendanceHistory() { return attendanceHistory; }
    public void setAttendanceHistory(List<AttendanceResponse> attendanceHistory) { this.attendanceHistory = attendanceHistory; }
    public long getTotalVisits() { return totalVisits; }
    public void setTotalVisits(long totalVisits) { this.totalVisits = totalVisits; }
    public BigDecimal getRevenueGenerated() { return revenueGenerated; }
    public void setRevenueGenerated(BigDecimal revenueGenerated) { this.revenueGenerated = revenueGenerated; }
    public LocalDate getUpcomingExpiry() { return upcomingExpiry; }
    public void setUpcomingExpiry(LocalDate upcomingExpiry) { this.upcomingExpiry = upcomingExpiry; }
}
