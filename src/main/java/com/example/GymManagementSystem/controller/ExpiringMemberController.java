package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.ExpiringMemberResponse;
import com.example.GymManagementSystem.dto.ExpiringMembersGroupedResponse;
import com.example.GymManagementSystem.service.ExpiringMemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/expiring-members")
public class ExpiringMemberController {

    private final ExpiringMemberService expiringMemberService;

    public ExpiringMemberController(ExpiringMemberService expiringMemberService) {
        this.expiringMemberService = expiringMemberService;
    }

    @GetMapping("/grouped")
    public ExpiringMembersGroupedResponse grouped() {
        return expiringMemberService.getGroupedExpiringMembers();
    }

    @GetMapping("/today")
    public List<ExpiringMemberResponse> today() {
        LocalDate today = LocalDate.now();
        return expiringMemberService.findExpiring(today, today);
    }

    @GetMapping("/3-days")
    public List<ExpiringMemberResponse> threeDays() {
        LocalDate today = LocalDate.now();
        return expiringMemberService.findExpiring(today, today.plusDays(3));
    }

    @GetMapping("/7-days")
    public List<ExpiringMemberResponse> sevenDays() {
        LocalDate today = LocalDate.now();
        return expiringMemberService.findExpiring(today, today.plusDays(7));
    }
}
