package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.MemberPhotoRequest;
import com.example.GymManagementSystem.dto.MemberProfileResponse;
import com.example.GymManagementSystem.dto.MemberResponse;
import com.example.GymManagementSystem.service.MemberProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/members")
public class MemberProfileController {

    private final MemberProfileService memberProfileService;

    public MemberProfileController(MemberProfileService memberProfileService) {
        this.memberProfileService = memberProfileService;
    }

    @GetMapping("/{memberId}/profile")
    public MemberProfileResponse getProfile(@PathVariable Long memberId) {
        return memberProfileService.getProfile(memberId);
    }

    @PutMapping("/{memberId}/photo")
    public MemberResponse updatePhoto(@PathVariable Long memberId,
                                      @Valid @RequestBody MemberPhotoRequest request) {
        return memberProfileService.updatePhoto(memberId, request);
    }
}
