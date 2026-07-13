package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.EnrollmentResponse;
import com.example.GymManagementSystem.dto.MemberResponse;
import com.example.GymManagementSystem.dto.MembershipResponse;
import com.example.GymManagementSystem.dto.PaymentResponse;
import com.example.GymManagementSystem.dto.RenewalPaymentRequest;
import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.model.MembershipPlan;
import com.example.GymManagementSystem.model.Payment;
import com.example.GymManagementSystem.repository.MemberRepository;
import com.example.GymManagementSystem.repository.MembershipPlanRepository;
import com.example.GymManagementSystem.repository.MembershipRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RenewalService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final PaymentRepository paymentRepository;
    private final OfferService offerService;

    public RenewalService(MemberRepository memberRepository,
                          MembershipRepository membershipRepository,
                          MembershipPlanRepository membershipPlanRepository,
                          PaymentRepository paymentRepository,
                          OfferService offerService) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.paymentRepository = paymentRepository;
        this.offerService = offerService;
    }

    @Transactional
    public EnrollmentResponse renew(Long memberId, RenewalPaymentRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id " + memberId));

        MembershipPlan plan = membershipPlanRepository.findById(request.getPlanId())
                .filter(MembershipPlan::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Active membership plan not found with id " + request.getPlanId()));

        List<Membership> activeMemberships = membershipRepository.findByMemberIdAndStatus(memberId, "ACTIVE");
        for (Membership activeMembership : activeMemberships) {
            activeMembership.setStatus("RENEWED");
            membershipRepository.save(activeMembership);
        }

        LocalDate joinDate = LocalDate.now();
        LocalDate expiryDate = joinDate.plusMonths(plan.getDurationMonths());
        BigDecimal discountedPrice = offerService.calculateFinalPrice(plan);

        Membership membership = new Membership(
                plan.getName(),
                joinDate,
                expiryDate,
                "ACTIVE",
                discountedPrice,
                member
        );
        membership.setPlan(plan);
        Membership savedMembership = membershipRepository.save(membership);

        Payment payment = new Payment();
        payment.setAmount(discountedPrice);
        payment.setPaymentMode(request.getPaymentMode());
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentDate(LocalDate.now());
        payment.setMember(member);
        payment.setMembership(savedMembership);
        Payment savedPayment = paymentRepository.save(payment);

        EnrollmentResponse response = new EnrollmentResponse();
        response.setMember(MemberResponse.from(member));
        response.setMembership(MembershipResponse.from(savedMembership));
        response.setPayment(PaymentResponse.from(savedPayment));
        response.setReceiptNumber(String.format("MMR-%05d", savedPayment.getId()));
        return response;
    }
}
