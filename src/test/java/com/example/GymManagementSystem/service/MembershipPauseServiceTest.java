package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.MembershipPauseRequest;
import com.example.GymManagementSystem.dto.MembershipPauseResponse;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.repository.MembershipPauseRepository;
import com.example.GymManagementSystem.repository.MembershipRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MembershipPauseServiceTest {

    @Test
    void pauseMembershipShouldExtendExpiryAndPersistPauseDays() {
        MembershipRepository membershipRepository = mock(MembershipRepository.class);
        MembershipPauseRepository membershipPauseRepository = mock(MembershipPauseRepository.class);
        MembershipPauseService membershipPauseService = new MembershipPauseService(membershipRepository, membershipPauseRepository, mock(AuditLogService.class));

        Member member = new Member();
        member.setId(1L);

        Membership membership = new Membership();
        membership.setId(5L);
        membership.setMember(member);
        membership.setExpiryDate(LocalDate.of(2026, 7, 1));

        when(membershipRepository.findById(5L)).thenReturn(Optional.of(membership));
        when(membershipPauseRepository.existsOverlap(5L, LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 20))).thenReturn(false);
        when(membershipPauseRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MembershipPauseRequest request = new MembershipPauseRequest();
        request.setMembershipId(5L);
        request.setPauseStartDate(LocalDate.of(2026, 7, 10));
        request.setPauseEndDate(LocalDate.of(2026, 7, 20));
        request.setReason("Vacation");

        MembershipPauseResponse response = membershipPauseService.pauseMembership(request);

        assertThat(response.getPauseDays()).isEqualTo(10);
        assertThat(membership.getExpiryDate()).isEqualTo(LocalDate.of(2026, 7, 11));
    }
}
