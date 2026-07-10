package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.MemberRequest;
import com.example.GymManagementSystem.dto.MemberResponse;
import com.example.GymManagementSystem.dto.MemberStatusResponse;
import com.example.GymManagementSystem.dto.MembershipResponse;
import com.example.GymManagementSystem.dto.RenewRequest;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.service.MemberService;
import com.example.GymManagementSystem.service.MembershipService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MembershipService membershipService;

    public MemberController(MemberService memberService,
                            MembershipService membershipService) {
        this.memberService = memberService;
        this.membershipService = membershipService;
    }

    @PostMapping
    public MemberResponse addMember(@Valid @RequestBody MemberRequest member) {
        throw new BadRequestException("Members must be created through enrollment. Use POST /enrollment to create the member, membership, and payment together.");
    }

    @GetMapping
    public List<MemberResponse> getAllMembers() {
        return memberService.getAllMembers();
    }

    @GetMapping("/active")
    public List<MemberStatusResponse> getActiveMembers() {
        return memberService.getActiveMembers();
    }

    @GetMapping("/expired")
    public List<MemberStatusResponse> getExpiredMembers() {
        return memberService.getExpiredMembers();
    }

    @GetMapping("/expiring-soon")
    public List<MemberStatusResponse> getExpiringSoonMembers() {
        return memberService.getExpiringSoonMembers();
    }

    @GetMapping("/search")
    public List<MemberResponse> searchMembers(@RequestParam String query) {
        return memberService.searchMembers(query);
    }

    @DeleteMapping("/{id}")
    public String deleteMember(@PathVariable Long id) {

        memberService.deleteMember(id);

        return "Member deleted successfully";
    }

    @PutMapping("/{id}")
    public MemberResponse updateMember(@PathVariable Long id,
            @Valid @RequestBody MemberRequest updatedMember) {

        return memberService.updateMember(id, updatedMember);
    }

    @PutMapping("/{id}/renew")
    public MembershipResponse renewMembership(@PathVariable Long id,
            @Valid @RequestBody RenewRequest request) {

        return membershipService.renewMembership(id, request);
    }
}
