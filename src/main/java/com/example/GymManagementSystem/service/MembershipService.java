package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.MembershipRequest;
import com.example.GymManagementSystem.dto.MembershipResponse;
import com.example.GymManagementSystem.dto.RenewRequest;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.repository.MemberRepository;
import com.example.GymManagementSystem.repository.MembershipRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;

    public MembershipService(MembershipRepository membershipRepository,
                             MemberRepository memberRepository) {

        this.membershipRepository = membershipRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MembershipResponse createMembership(MembershipRequest request) {
        Long memberId = requireId(request.getMemberId(), "Member id is required");

        Member member = memberRepository
                .findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + memberId));

        List<Membership> activeMemberships =
        membershipRepository
                .findByMemberIdAndStatus(
                        memberId,
                        "ACTIVE"
                );

        for(Membership membership : activeMemberships) {

            membership.setStatus("RENEWED");

            membershipRepository.save(membership);
        }

        LocalDate expiryDate = calculateExpiryDate(request);

        String status = expiryDate.isBefore(LocalDate.now()) ? "EXPIRED" : "ACTIVE";

        Membership membership = new Membership(
                request.getPlanType(),
                request.getJoinDate(),
                expiryDate,
                status,
                request.getPlanPrice(),
                member
        );

        BigDecimal totalAmount = request.getPlanPrice() == null ? BigDecimal.ZERO : request.getPlanPrice();
        BigDecimal amountPaid = request.getAmountPaid() == null ? BigDecimal.ZERO : request.getAmountPaid();

        if (amountPaid.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Amount paid cannot be negative");
        }
        if (amountPaid.compareTo(totalAmount) > 0) {
            throw new BadRequestException("Amount paid cannot exceed plan price");
        }

        membership.setTotalAmount(totalAmount);
        membership.setAmountPaid(amountPaid);
        membership.setBalanceAmount(totalAmount.subtract(amountPaid).max(BigDecimal.ZERO));
        membership.setPaymentStatus(Membership.resolvePaymentStatus(totalAmount, amountPaid));

        return MembershipResponse.from(membershipRepository.save(membership));
    }

    public List<MembershipResponse> getActiveMemberships() {

        return membershipRepository
                .findByStatus("ACTIVE")
                .stream()
                .map(MembershipResponse::from)
                .toList();
    }

    public List<MembershipResponse> getHistory(Long memberId) {
        Long id = requireId(memberId, "Member id is required");

        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found with id " + id);
        }

        return membershipRepository
                .findByMemberIdOrderByJoinDateDescIdDesc(id)
                .stream()
                .map(MembershipResponse::from)
                .toList();
    }

    public MembershipResponse renewMembership(Long memberId, RenewRequest request) {
        MembershipRequest membershipRequest = new MembershipRequest();
        membershipRequest.setMemberId(memberId);
        membershipRequest.setPlanType(request.getPlanType());
        membershipRequest.setPlanPrice(request.getPlanPrice());
        membershipRequest.setJoinDate(LocalDate.now());
        return createMembership(membershipRequest);
    }

    private LocalDate calculateExpiryDate(MembershipRequest request) {
        String planType = request.getPlanType().trim().toUpperCase();
        return switch (planType) {
            case "1 MONTH" -> request.getJoinDate().plusMonths(1);
            case "3 MONTH" -> request.getJoinDate().plusMonths(3);
            case "6 MONTH" -> request.getJoinDate().plusMonths(6);
            case "12 MONTH" -> request.getJoinDate().plusMonths(12);
            default -> throw new BadRequestException("Unsupported plan type: " + request.getPlanType());
        };
    }

    private Long requireId(Long id, String message) {
        if (id == null) {
            throw new BadRequestException(message);
        }
        return id;
    }
}


