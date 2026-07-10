package com.example.GymManagementSystem.dto;

import java.math.BigDecimal;

public class DashboardStatsResponse {

    private long totalMembers;
    private long activeMembers;
    private long expiredMembers;
    private long membershipsExpiringSoon;
    private long todaysCheckIns;
    private BigDecimal todaysRevenue;
    private BigDecimal weeklyRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal yearlyRevenue;
    private BigDecimal lifetimeRevenue;

    public long getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(long totalMembers) {
        this.totalMembers = totalMembers;
    }

    public long getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(long activeMembers) {
        this.activeMembers = activeMembers;
    }

    public long getExpiredMembers() {
        return expiredMembers;
    }

    public void setExpiredMembers(long expiredMembers) {
        this.expiredMembers = expiredMembers;
    }

    public long getMembershipsExpiringSoon() {
        return membershipsExpiringSoon;
    }

    public void setMembershipsExpiringSoon(long membershipsExpiringSoon) {
        this.membershipsExpiringSoon = membershipsExpiringSoon;
    }

    public long getTodaysCheckIns() {
        return todaysCheckIns;
    }

    public void setTodaysCheckIns(long todaysCheckIns) {
        this.todaysCheckIns = todaysCheckIns;
    }

    public BigDecimal getTodaysRevenue() {
        return todaysRevenue;
    }

    public void setTodaysRevenue(BigDecimal todaysRevenue) {
        this.todaysRevenue = todaysRevenue;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public BigDecimal getWeeklyRevenue() {
        return weeklyRevenue;
    }

    public void setWeeklyRevenue(BigDecimal weeklyRevenue) {
        this.weeklyRevenue = weeklyRevenue;
    }

    public BigDecimal getYearlyRevenue() {
        return yearlyRevenue;
    }

    public void setYearlyRevenue(BigDecimal yearlyRevenue) {
        this.yearlyRevenue = yearlyRevenue;
    }

    public BigDecimal getLifetimeRevenue() {
        return lifetimeRevenue;
    }

    public void setLifetimeRevenue(BigDecimal lifetimeRevenue) {
        this.lifetimeRevenue = lifetimeRevenue;
    }
}
