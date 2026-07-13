package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.MembershipPause;

import java.time.LocalDate;

public class MembershipPauseResponse {

    private Long id;
    private Long membershipId;
    private LocalDate pauseStartDate;
    private LocalDate pauseEndDate;
    private int pauseDays;
    private String reason;
    private LocalDate newExpiryDate;

    public static MembershipPauseResponse from(MembershipPause pause, LocalDate newExpiryDate) {
        MembershipPauseResponse response = new MembershipPauseResponse();
        response.setId(pause.getId());
        response.setMembershipId(pause.getMembership() != null ? pause.getMembership().getId() : null);
        response.setPauseStartDate(pause.getPauseStartDate());
        response.setPauseEndDate(pause.getPauseEndDate());
        response.setPauseDays(pause.getPauseDays());
        response.setReason(pause.getReason());
        response.setNewExpiryDate(newExpiryDate);
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(Long membershipId) {
        this.membershipId = membershipId;
    }

    public LocalDate getPauseStartDate() {
        return pauseStartDate;
    }

    public void setPauseStartDate(LocalDate pauseStartDate) {
        this.pauseStartDate = pauseStartDate;
    }

    public LocalDate getPauseEndDate() {
        return pauseEndDate;
    }

    public void setPauseEndDate(LocalDate pauseEndDate) {
        this.pauseEndDate = pauseEndDate;
    }

    public int getPauseDays() {
        return pauseDays;
    }

    public void setPauseDays(int pauseDays) {
        this.pauseDays = pauseDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getNewExpiryDate() {
        return newExpiryDate;
    }

    public void setNewExpiryDate(LocalDate newExpiryDate) {
        this.newExpiryDate = newExpiryDate;
    }
}
