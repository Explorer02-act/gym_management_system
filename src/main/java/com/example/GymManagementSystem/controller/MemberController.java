package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.RenewRequest;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.service.MemberService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public Member addMember(@Valid @RequestBody Member member) {
        return memberService.addMember(member);
    }

    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    @DeleteMapping("/{id}")
    public String deleteMember(@PathVariable Long id) {

        memberService.deleteMember(id);

        return "Member deleted successfully";
    }

    @PutMapping("/{id}")
    public Member updateMember(@PathVariable Long id,
            @RequestBody Member updatedMember) {

        return memberService.updateMember(id, updatedMember);
    }

    @PutMapping("/{id}/renew")
    public Membership renewMembership(@PathVariable Long id,
            @Valid @RequestBody RenewRequest request) {

        return memberService.renewMembership(id, request.getPlanType());
    }
}
