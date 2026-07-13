package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.MembershipPlanRequest;
import com.example.GymManagementSystem.dto.MembershipPlanResponse;
import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.MembershipPlan;
import com.example.GymManagementSystem.repository.MembershipPlanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MembershipPlanService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final OfferService offerService;

    public MembershipPlanService(MembershipPlanRepository membershipPlanRepository, OfferService offerService) {
        this.membershipPlanRepository = membershipPlanRepository;
        this.offerService = offerService;
    }

    public List<MembershipPlanResponse> getActivePlans() {
        seedDefaultPlansIfEmpty();
        return membershipPlanRepository.findByActiveTrueOrderByDurationMonthsAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<MembershipPlanResponse> getAllPlans() {
        seedDefaultPlansIfEmpty();
        return membershipPlanRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MembershipPlanResponse createPlan(MembershipPlanRequest request) {
        MembershipPlan plan = new MembershipPlan();
        apply(plan, request);
        return toResponse(membershipPlanRepository.save(plan));
    }

    public MembershipPlanResponse updatePlan(Long id, MembershipPlanRequest request) {
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id " + id));
        apply(plan, request);
        return toResponse(membershipPlanRepository.save(plan));
    }

    public void deletePlan(Long id) {
        MembershipPlan plan = membershipPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership plan not found with id " + id));
        plan.setActive(false);
        membershipPlanRepository.save(plan);
    }

    private MembershipPlanResponse toResponse(MembershipPlan plan) {
        MembershipPlanResponse response = MembershipPlanResponse.from(plan);
        response.setDisplayPrice(offerService.calculateFinalPrice(plan));
        return response;
    }

    private void apply(MembershipPlan plan, MembershipPlanRequest request) {
        plan.setName(request.getName());
        plan.setDurationMonths(request.getDurationMonths());
        plan.setActualPrice(request.getActualPrice());
        plan.setDisplayPrice(request.getDisplayPrice());
        plan.setDescription(request.getDescription());
        plan.setBadge(request.getBadge());
        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }
    }

    private void seedDefaultPlansIfEmpty() {
        if (membershipPlanRepository.count() > 0) {
            return;
        }

        createSeed("1 MONTH", 1, "3000", "2000", "Starter Plan", "Starter Plan");
        createSeed("3 MONTHS", 3, "7000", "5000", "Most Popular", "Most Popular");
        createSeed("6 MONTHS", 6, "15000", "10000", "Best Value", "Best Value");
        createSeed("12 MONTHS", 12, "30000", "18000", "Recommended", "Recommended");
    }

    private void createSeed(String name,
                            int durationMonths,
                            String actualPrice,
                            String displayPrice,
                            String description,
                            String badge) {
        MembershipPlan plan = new MembershipPlan();
        plan.setName(name);
        plan.setDurationMonths(durationMonths);
        plan.setActualPrice(new BigDecimal(actualPrice));
        plan.setDisplayPrice(new BigDecimal(displayPrice));
        plan.setDescription(description);
        plan.setBadge(badge);
        plan.setActive(true);
        membershipPlanRepository.save(plan);
    }
}
