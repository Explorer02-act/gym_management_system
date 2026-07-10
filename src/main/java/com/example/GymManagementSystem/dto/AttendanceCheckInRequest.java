package com.example.GymManagementSystem.dto;

import jakarta.validation.constraints.NotNull;

public class AttendanceCheckInRequest {

    @NotNull(message = "Member id is required")
    private Long memberId;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
