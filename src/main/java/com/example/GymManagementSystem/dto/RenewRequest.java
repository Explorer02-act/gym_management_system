package com.example.GymManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;

public class RenewRequest {

    @NotBlank(message = "Plan type is required")
    private String planType;

    public RenewRequest() {
    }

    public RenewRequest(String planType) {
        this.planType = planType;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }
}
