package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.MembershipPlanRequest;
import com.example.GymManagementSystem.dto.MembershipPlanResponse;
import com.example.GymManagementSystem.service.MembershipPlanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/plans")
public class MembershipPlanController {

    private final MembershipPlanService membershipPlanService;

    public MembershipPlanController(MembershipPlanService membershipPlanService) {
        this.membershipPlanService = membershipPlanService;
    }

    @GetMapping
    public List<MembershipPlanResponse> getActivePlans() {
        return membershipPlanService.getActivePlans();
    }

    @GetMapping("/all")
    public List<MembershipPlanResponse> getAllPlans() {
        return membershipPlanService.getAllPlans();
    }

    @PostMapping
    public MembershipPlanResponse createPlan(@Valid @RequestBody MembershipPlanRequest request) {
        return membershipPlanService.createPlan(request);
    }

    @PutMapping("/{id}")
    public MembershipPlanResponse updatePlan(@PathVariable Long id,
                                             @Valid @RequestBody MembershipPlanRequest request) {
        return membershipPlanService.updatePlan(id, request);
    }

    @DeleteMapping("/{id}")
    public String deletePlan(@PathVariable Long id) {
        membershipPlanService.deletePlan(id);
        return "Plan disabled successfully";
    }
}
