package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.MembershipPlan;

import java.math.BigDecimal;

public class MembershipPlanResponse {

    private Long id;
    private String name;
    private Integer durationMonths;
    private BigDecimal actualPrice;
    private BigDecimal displayPrice;
    private String description;
    private String badge;
    private boolean active;

    public static MembershipPlanResponse from(MembershipPlan plan) {
        MembershipPlanResponse response = new MembershipPlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setDurationMonths(plan.getDurationMonths());
        response.setActualPrice(plan.getActualPrice());
        response.setDisplayPrice(plan.getDisplayPrice());
        response.setDescription(plan.getDescription());
        response.setBadge(plan.getBadge());
        response.setActive(plan.isActive());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public BigDecimal getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
        this.actualPrice = actualPrice;
    }

    public BigDecimal getDisplayPrice() {
        return displayPrice;
    }

    public void setDisplayPrice(BigDecimal displayPrice) {
        this.displayPrice = displayPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
