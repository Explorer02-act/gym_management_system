package com.example.GymManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class MembershipPauseRequest {

    @NotNull(message = "Membership id is required")
    private Long membershipId;

    @NotNull(message = "Pause start date is required")
    private LocalDate pauseStartDate;

    @NotNull(message = "Pause end date is required")
    private LocalDate pauseEndDate;

    @NotBlank(message = "Reason is required")
    private String reason;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
