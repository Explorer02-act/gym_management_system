package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.MembershipPauseRequest;
import com.example.GymManagementSystem.dto.MembershipPauseResponse;
import com.example.GymManagementSystem.service.MembershipPauseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/membership")
public class MembershipPauseController {

    private final MembershipPauseService membershipPauseService;

    public MembershipPauseController(MembershipPauseService membershipPauseService) {
        this.membershipPauseService = membershipPauseService;
    }

    @PostMapping("/pause")
    public MembershipPauseResponse pauseMembership(@Valid @RequestBody MembershipPauseRequest request) {
        return membershipPauseService.pauseMembership(request);
    }

    @GetMapping("/paused")
    public List<MembershipPauseResponse> getPausedMemberships() {
        return membershipPauseService.getPausedMemberships();
    }

    @GetMapping("/pause-history/{membershipId}")
    public List<MembershipPauseResponse> getPauseHistory(@PathVariable Long membershipId) {
        return membershipPauseService.getPauseHistory(membershipId);
    }
}
