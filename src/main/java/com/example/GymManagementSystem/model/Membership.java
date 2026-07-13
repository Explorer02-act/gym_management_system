package com.example.GymManagementSystem.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Membership extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private MembershipPlan plan;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public Membership() {
    }

    public Membership(String planType,
                      LocalDate joinDate,
                      LocalDate expiryDate,
                      String status,
                      BigDecimal planPrice,
                      Member member) {

        this.planType = planType;
        this.joinDate = joinDate;
        this.expiryDate = expiryDate;
        this.status = status;
        this.planPrice = planPrice;
        this.member = member;
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

    public MembershipPlan getPlan() {
        return plan;
    }

    public void setPlan(MembershipPlan plan) {
        this.plan = plan;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void applyPayment(BigDecimal paymentAmount, BigDecimal totalAmount) {
        BigDecimal safeTotal = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        BigDecimal safePaid = amountPaid == null ? BigDecimal.ZERO : amountPaid;
        BigDecimal updatedPaid = safePaid.add(paymentAmount);
        this.amountPaid = updatedPaid;
        this.totalAmount = safeTotal;
        this.balanceAmount = safeTotal.subtract(updatedPaid).max(BigDecimal.ZERO);
        this.paymentStatus = resolvePaymentStatus(safeTotal, updatedPaid);
    }

    public static String resolvePaymentStatus(BigDecimal totalAmount, BigDecimal amountPaid) {
        BigDecimal safeTotal = totalAmount == null ? BigDecimal.ZERO : totalAmount;
        BigDecimal safePaid = amountPaid == null ? BigDecimal.ZERO : amountPaid;

        if (safePaid.compareTo(safeTotal) >= 0) {
            return "PAID";
        }
        if (safePaid.compareTo(BigDecimal.ZERO) <= 0) {
            return "PENDING";
        }
        return "PARTIAL";
    }
}
