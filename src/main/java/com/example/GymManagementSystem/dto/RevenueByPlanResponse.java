package com.example.GymManagementSystem.dto;

import java.math.BigDecimal;

public class RevenueByPlanResponse {

    private String planName;
    private BigDecimal totalRevenue;
    private long paymentCount;

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public long getPaymentCount() { return paymentCount; }
    public void setPaymentCount(long paymentCount) { this.paymentCount = paymentCount; }
}
