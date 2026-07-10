package com.example.GymManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;

public class AttendanceLookupCheckInRequest {

    @NotBlank(message = "Member code or phone is required")
    private String memberCodeOrPhone;

    public String getMemberCodeOrPhone() {
        return memberCodeOrPhone;
    }

    public void setMemberCodeOrPhone(String memberCodeOrPhone) {
        this.memberCodeOrPhone = memberCodeOrPhone;
    }
}
