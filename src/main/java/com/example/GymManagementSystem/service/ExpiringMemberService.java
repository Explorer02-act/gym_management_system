package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.ExpiringMemberResponse;
import com.example.GymManagementSystem.dto.ExpiringMembersGroupedResponse;
import com.example.GymManagementSystem.dto.MemberResponse;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.repository.MembershipRepository;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ExpiringMemberService {

    private final MembershipRepository membershipRepository;

    public ExpiringMemberService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    public ExpiringMembersGroupedResponse getGroupedExpiringMembers() {
        LocalDate today = LocalDate.now();
        ExpiringMembersGroupedResponse response = new ExpiringMembersGroupedResponse();
        response.setExpiresToday(findExpiring(today, today));
        response.setExpiresIn3Days(findExpiring(today.plusDays(1), today.plusDays(3)));
        response.setExpiresIn7Days(findExpiring(today.plusDays(4), today.plusDays(7)));
        return response;
    }

    public List<ExpiringMemberResponse> findExpiring(LocalDate startDate, LocalDate endDate) {
        return membershipRepository.findByStatusAndExpiryDateBetweenOrderByExpiryDateAsc("ACTIVE", startDate, endDate)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ExpiringMemberResponse toResponse(Membership membership) {
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), membership.getExpiryDate());
        ExpiringMemberResponse response = new ExpiringMemberResponse();
        response.setMember(MemberResponse.from(membership.getMember()));
        response.setExpiryDate(membership.getExpiryDate());
        response.setDaysRemaining(daysRemaining);
        response.setWhatsappUrl(buildWhatsappUrl(membership, daysRemaining));
        return response;
    }

    private String buildWhatsappUrl(Membership membership, long daysRemaining) {
        String phone = membership.getMember().getPhone();
        String message = "Hi " + membership.getMember().getName() + ",\n"
                + "Your MUSCLE MONSTERZ membership expires in " + daysRemaining + " day(s).\n"
                + "Please renew your membership.\n\nGym Team";
        return "https://wa.me/91" + phone + "?text=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
    }
}
