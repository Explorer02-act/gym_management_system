package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.Member;

import java.time.LocalDate;

public class MemberStatusResponse {

    private MemberResponse member;
    private String status;
    private LocalDate expiryDate;
    private Long daysRemaining;

    public static MemberStatusResponse of(Member member, String status, LocalDate expiryDate, Long daysRemaining) {
        MemberStatusResponse response = new MemberStatusResponse();
        response.setMember(MemberResponse.from(member));
        response.setStatus(status);
        response.setExpiryDate(expiryDate);
        response.setDaysRemaining(daysRemaining);
        return response;
    }

    public MemberResponse getMember() {
        return member;
    }

    public void setMember(MemberResponse member) {
        this.member = member;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Long daysRemaining) {
        this.daysRemaining = daysRemaining;
    }
}
