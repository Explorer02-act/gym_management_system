package com.example.GymManagementSystem.dto;

import java.time.LocalDate;

public class ExpiringMemberResponse {

    private MemberResponse member;
    private LocalDate expiryDate;
    private long daysRemaining;
    private String whatsappUrl;

    public MemberResponse getMember() { return member; }
    public void setMember(MemberResponse member) { this.member = member; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }
    public String getWhatsappUrl() { return whatsappUrl; }
    public void setWhatsappUrl(String whatsappUrl) { this.whatsappUrl = whatsappUrl; }
}
