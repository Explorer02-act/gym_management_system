package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.RevenueByPlanResponse;
import com.example.GymManagementSystem.dto.RevenueSummaryResponse;
import com.example.GymManagementSystem.service.RevenueAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/revenue")
public class RevenueAnalyticsController {

    private final RevenueAnalyticsService revenueAnalyticsService;

    public RevenueAnalyticsController(RevenueAnalyticsService revenueAnalyticsService) {
        this.revenueAnalyticsService = revenueAnalyticsService;
    }

    @GetMapping("/summary")
    public RevenueSummaryResponse summary() {
        return revenueAnalyticsService.getSummary();
    }

    @GetMapping("/by-plan")
    public List<RevenueByPlanResponse> byPlan() {
        return revenueAnalyticsService.getRevenueByPlan();
    }
}
