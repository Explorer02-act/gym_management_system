package com.example.GymManagementSystem.dto;

import java.math.BigDecimal;
import java.util.List;

public class RevenueSummaryResponse {

    private BigDecimal todaysRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal yearlyRevenue;
    private BigDecimal lifetimeRevenue;
    private BigDecimal todaysCashRevenue;
    private BigDecimal todaysUpiRevenue;
    private BigDecimal monthlyCashRevenue;
    private BigDecimal monthlyUpiRevenue;
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
    public BigDecimal getTodaysCashRevenue() { return todaysCashRevenue; }
    public void setTodaysCashRevenue(BigDecimal todaysCashRevenue) { this.todaysCashRevenue = todaysCashRevenue; }
    public BigDecimal getTodaysUpiRevenue() { return todaysUpiRevenue; }
    public void setTodaysUpiRevenue(BigDecimal todaysUpiRevenue) { this.todaysUpiRevenue = todaysUpiRevenue; }
    public BigDecimal getMonthlyCashRevenue() { return monthlyCashRevenue; }
    public void setMonthlyCashRevenue(BigDecimal monthlyCashRevenue) { this.monthlyCashRevenue = monthlyCashRevenue; }
    public BigDecimal getMonthlyUpiRevenue() { return monthlyUpiRevenue; }
    public void setMonthlyUpiRevenue(BigDecimal monthlyUpiRevenue) { this.monthlyUpiRevenue = monthlyUpiRevenue; }
    public List<RevenueByPlanResponse> getRevenueByPlan() { return revenueByPlan; }
    public void setRevenueByPlan(List<RevenueByPlanResponse> revenueByPlan) { this.revenueByPlan = revenueByPlan; }
}
