package com.example.GymManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MembershipRequest {

    @NotNull(message = "Member id is required")
    private Long memberId;

    @NotBlank(message = "Plan type is required")
    private String planType;

    @NotNull(message = "Join date is required")
    private LocalDate joinDate;

    @NotNull(message = "Plan price is required")
    @PositiveOrZero(message = "Plan price cannot be negative")
    private BigDecimal planPrice;

    @PositiveOrZero(message = "Amount paid cannot be negative")
    private BigDecimal amountPaid;

    public MembershipRequest() {
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public BigDecimal getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(BigDecimal planPrice) {
        this.planPrice = planPrice;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }
}
