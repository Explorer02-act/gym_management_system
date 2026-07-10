package com.example.GymManagementSystem.dto;

import java.util.List;

public class ExpiringMembersGroupedResponse {

    private List<ExpiringMemberResponse> expiresToday;
    private List<ExpiringMemberResponse> expiresIn3Days;
    private List<ExpiringMemberResponse> expiresIn7Days;

    public List<ExpiringMemberResponse> getExpiresToday() { return expiresToday; }
    public void setExpiresToday(List<ExpiringMemberResponse> expiresToday) { this.expiresToday = expiresToday; }
    public List<ExpiringMemberResponse> getExpiresIn3Days() { return expiresIn3Days; }
    public void setExpiresIn3Days(List<ExpiringMemberResponse> expiresIn3Days) { this.expiresIn3Days = expiresIn3Days; }
    public List<ExpiringMemberResponse> getExpiresIn7Days() { return expiresIn7Days; }
    public void setExpiresIn7Days(List<ExpiringMemberResponse> expiresIn7Days) { this.expiresIn7Days = expiresIn7Days; }
}
