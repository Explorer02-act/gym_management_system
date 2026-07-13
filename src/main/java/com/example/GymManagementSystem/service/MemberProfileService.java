package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.AttendanceResponse;
import com.example.GymManagementSystem.dto.MemberProfileResponse;
import com.example.GymManagementSystem.dto.MemberResponse;
import com.example.GymManagementSystem.dto.MembershipPauseResponse;
import com.example.GymManagementSystem.dto.MembershipResponse;
import com.example.GymManagementSystem.dto.MemberPhotoRequest;
import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.repository.AttendanceRepository;
import com.example.GymManagementSystem.repository.MemberRepository;
import com.example.GymManagementSystem.repository.MembershipPauseRepository;
import com.example.GymManagementSystem.repository.MembershipRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberProfileService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPauseRepository membershipPauseRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;

    public MemberProfileService(MemberRepository memberRepository,
                                MembershipRepository membershipRepository,
                                MembershipPauseRepository membershipPauseRepository,
                                AttendanceRepository attendanceRepository,
                                PaymentRepository paymentRepository) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.membershipPauseRepository = membershipPauseRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
    }

    public MemberProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + memberId));

        List<Membership> memberships = membershipRepository.findByMemberIdOrderByJoinDateDescIdDesc(memberId);
        Membership currentMembership = memberships.stream().findFirst().orElse(null);

        MemberProfileResponse response = new MemberProfileResponse();
        response.setMember(MemberResponse.from(member));
        response.setMembershipHistory(memberships.stream().map(MembershipResponse::from).toList());
        response.setCurrentMembership(currentMembership != null ? MembershipResponse.from(currentMembership) : null);
        response.setUpcomingExpiry(currentMembership != null ? currentMembership.getExpiryDate() : null);
        response.setPauseHistory(currentMembership != null
                ? membershipPauseRepository.findByMembershipIdOrderByPauseStartDateDescIdDesc(currentMembership.getId())
                        .stream()
                        .map(pause -> MembershipPauseResponse.from(pause, currentMembership.getExpiryDate()))
                        .toList()
                : new ArrayList<>());
        response.setAttendanceHistory(attendanceRepository.findByMemberIdOrderByAttendanceDateDescCheckInTimeDesc(memberId)
                .stream()
                .map(AttendanceResponse::from)
                .toList());
        response.setTotalVisits(attendanceRepository.countByMemberId(memberId));
        response.setRevenueGenerated(paymentRepository.sumByMemberId(memberId));
        return response;
    }

    public MemberResponse updatePhoto(Long memberId, MemberPhotoRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + memberId));
        member.setPhotoUrl(request.getPhotoUrl());
        return MemberResponse.from(memberRepository.save(member));
    }
}
