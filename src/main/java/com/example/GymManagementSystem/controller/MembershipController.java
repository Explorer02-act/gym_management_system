package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.MembershipRequest;
import com.example.GymManagementSystem.dto.MembershipResponse;
import com.example.GymManagementSystem.service.MembershipService;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/memberships")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @PostMapping
    public MembershipResponse createMembership(
            @Valid @RequestBody MembershipRequest request) {

        return membershipService.createMembership(request);
    }

    @GetMapping("/active")
    public List<MembershipResponse> getActiveMemberships() {

        return membershipService
                .getActiveMemberships();
    }
    
    @GetMapping({"/member/{memberId}", "/history/{memberId}"})
    public List<MembershipResponse> getHistory(@PathVariable Long memberId) {

        return membershipService
                .getHistory(memberId);
    }
}
