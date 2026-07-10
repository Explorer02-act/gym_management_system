package com.example.GymManagementSystem.dto;

import java.math.BigDecimal;
import java.util.List;

public class RevenueSummaryResponse {

    private BigDecimal todaysRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal yearlyRevenue;
    private BigDecimal lifetimeRevenue;
    private List<RevenueByPlanResponse> revenueByPlan;

    public BigDecimal getTodaysRevenue() { return todaysRevenue; }
    public void setTodaysRevenue(BigDecimal todaysRevenue) { this.todaysRevenue = todaysRevenue; }
    public BigDecimal getWeeklyRevenue() { return weeklyRevenue; }
    public void setWeeklyRevenue(BigDecimal weeklyRevenue) { this.weeklyRevenue = weeklyRevenue; }
    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    public BigDecimal getYearlyRevenue() { return yearlyRevenue; }
    public void setYearlyRevenue(BigDecimal yearlyRevenue) { this.yearlyRevenue = yearlyRevenue; }
    public BigDecimal getLifetimeRevenue() { return lifetimeRevenue; }
    public void setLifetimeRevenue(BigDecimal lifetimeRevenue) { this.lifetimeRevenue = lifetimeRevenue; }
    public List<RevenueByPlanResponse> getRevenueByPlan() { return revenueByPlan; }
    public void setRevenueByPlan(List<RevenueByPlanResponse> revenueByPlan) { this.revenueByPlan = revenueByPlan; }
}
