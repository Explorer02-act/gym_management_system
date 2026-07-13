package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.EnrollmentRequest;
import com.example.GymManagementSystem.dto.EnrollmentResponse;
import com.example.GymManagementSystem.dto.MemberResponse;
import com.example.GymManagementSystem.dto.MembershipResponse;
import com.example.GymManagementSystem.dto.PaymentResponse;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.exception.DuplicateResourceException;
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
import java.util.UUID;

@Service
public class EnrollmentService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipPlanRepository membershipPlanRepository;
    private final PaymentRepository paymentRepository;
    private final OfferService offerService;
    private final AuditLogService auditLogService;

    public EnrollmentService(MemberRepository memberRepository,
                             MembershipRepository membershipRepository,
                             MembershipPlanRepository membershipPlanRepository,
                             PaymentRepository paymentRepository,
                             OfferService offerService,
                             AuditLogService auditLogService) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.membershipPlanRepository = membershipPlanRepository;
        this.paymentRepository = paymentRepository;
        this.offerService = offerService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        if (memberRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Member with phone number " + request.getPhone() + " already exists");
        }

        MembershipPlan plan = membershipPlanRepository.findById(request.getPlanId())
                .filter(MembershipPlan::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Active membership plan not found with id " + request.getPlanId()));

        BigDecimal discountedPrice = offerService.calculateFinalPrice(plan);
        BigDecimal initialAmount = request.getAmountPaid() == null ? discountedPrice : request.getAmountPaid();

        if (initialAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount paid must be greater than zero");
        }
        if (initialAmount.compareTo(discountedPrice) > 0) {
            throw new BadRequestException("Amount paid cannot exceed the plan price after offer discount");
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setPhone(request.getPhone());
        member.setEmergencyContactName(request.getEmergencyContactName());
        member.setEmergencyContactPhone(request.getEmergencyContactPhone());
        member.setMemberCode("PENDING-" + UUID.randomUUID());

        Member savedMember = memberRepository.save(member);
        savedMember.setMemberCode(String.format("GYM%03d", savedMember.getId()));
        savedMember = memberRepository.save(savedMember);

        List<Membership> activeMemberships = membershipRepository.findByMemberIdAndStatus(savedMember.getId(), "ACTIVE");
        for (Membership activeMembership : activeMemberships) {
            activeMembership.setStatus("RENEWED");
            membershipRepository.save(activeMembership);
        }

        LocalDate joinDate = LocalDate.now();
        LocalDate expiryDate = joinDate.plusMonths(plan.getDurationMonths());

        Membership membership = new Membership(
                plan.getName(),
                joinDate,
                expiryDate,
                expiryDate.isBefore(LocalDate.now()) ? "EXPIRED" : "ACTIVE",
                discountedPrice,
                savedMember
        );
        membership.setPlan(plan);
        membership.setTotalAmount(discountedPrice);
        membership.setAmountPaid(initialAmount);
        membership.setBalanceAmount(discountedPrice.subtract(initialAmount).max(BigDecimal.ZERO));
        membership.setPaymentStatus(Membership.resolvePaymentStatus(discountedPrice, initialAmount));
        Membership savedMembership = membershipRepository.save(membership);

        Payment payment = new Payment();
        payment.setAmount(initialAmount);
        payment.setPaymentMode(request.getPaymentMode());
        payment.setTransactionId(request.getTransactionId());
        payment.setPaymentDate(LocalDate.now());
        payment.setMember(savedMember);
        payment.setMembership(savedMembership);
        Payment savedPayment = paymentRepository.save(payment);

        EnrollmentResponse response = new EnrollmentResponse();
        response.setMember(MemberResponse.from(savedMember));
        response.setMembership(MembershipResponse.from(savedMembership));
        response.setPayment(PaymentResponse.from(savedPayment));
        response.setReceiptNumber(String.format("MMR-%05d", savedPayment.getId()));
        return response;
    }
}
