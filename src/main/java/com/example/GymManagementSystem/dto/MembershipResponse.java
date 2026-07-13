package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.Membership;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MembershipResponse {

    private Long id;
    private String planType;
    private LocalDate joinDate;
    private LocalDate expiryDate;
    private String status;
    private BigDecimal planPrice;
    private BigDecimal totalAmount;
    private BigDecimal amountPaid;
    private BigDecimal balanceAmount;
    private String paymentStatus;
    private MemberResponse member;

    public static MembershipResponse from(Membership membership) {
        MembershipResponse response = new MembershipResponse();
        response.setId(membership.getId());
        response.setPlanType(membership.getPlanType());
        response.setJoinDate(membership.getJoinDate());
        response.setExpiryDate(membership.getExpiryDate());
        response.setStatus(membership.getStatus());
        response.setPlanPrice(membership.getPlanPrice());
        response.setTotalAmount(membership.getTotalAmount());
        response.setAmountPaid(membership.getAmountPaid());
        response.setBalanceAmount(membership.getBalanceAmount());
        response.setPaymentStatus(membership.getPaymentStatus());
        response.setMember(MemberResponse.from(membership.getMember()));
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(BigDecimal planPrice) {
        this.planPrice = planPrice;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }

    public BigDecimal getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(BigDecimal balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public MemberResponse getMember() {
        return member;
    }

    public void setMember(MemberResponse member) {
        this.member = member;
    }
}
