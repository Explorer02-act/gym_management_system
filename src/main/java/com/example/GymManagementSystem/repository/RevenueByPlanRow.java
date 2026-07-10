package com.example.GymManagementSystem.repository;

import java.math.BigDecimal;

public interface RevenueByPlanRow {

    String getPlanName();

    BigDecimal getTotalRevenue();

    Long getPaymentCount();
}
