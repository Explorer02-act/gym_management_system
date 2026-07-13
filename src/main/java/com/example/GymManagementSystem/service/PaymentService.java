package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.PaymentRequest;
import com.example.GymManagementSystem.dto.PaymentResponse;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.model.Payment;
import com.example.GymManagementSystem.repository.MembershipRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MembershipRepository membershipRepository;

    public PaymentService(PaymentRepository paymentRepository, MembershipRepository membershipRepository) {
        this.paymentRepository = paymentRepository;
        this.membershipRepository = membershipRepository;
    }

    @Transactional
    public PaymentResponse addPayment(PaymentRequest request) {
        if (request.getMembershipId() == null) {
            throw new BadRequestException("Membership id is required");
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be positive");
        }

        Membership membership = membershipRepository.findById(request.getMembershipId())
                .orElseThrow(() -> new BadRequestException("Membership not found with id " + request.getMembershipId()));

        BigDecimal totalAmount = membership.getTotalAmount() != null ? membership.getTotalAmount() : membership.getPlanPrice();
        BigDecimal currentBalance = membership.getBalanceAmount() != null ? membership.getBalanceAmount() : totalAmount.subtract(membership.getAmountPaid() == null ? BigDecimal.ZERO : membership.getAmountPaid()).max(BigDecimal.ZERO);
        if (request.getAmount().compareTo(currentBalance) > 0) {
            throw new BadRequestException("Payment amount cannot exceed outstanding balance");
        }

        Payment payment = new Payment();
        payment.setMembership(membership);
        payment.setMember(membership.getMember());
        payment.setAmount(request.getAmount());
        payment.setPaymentMode(request.getPaymentMode());
        payment.setTransactionId(request.getTransactionId());
        payment.setRemarks(request.getRemarks());
        payment.setPaymentDate(LocalDate.now());

        Payment savedPayment = paymentRepository.save(payment);

        membership.applyPayment(request.getAmount(), totalAmount);
        membership.setBalanceAmount(totalAmount.subtract(membership.getAmountPaid()).max(BigDecimal.ZERO));
        membership.setPaymentStatus(Membership.resolvePaymentStatus(totalAmount, membership.getAmountPaid()));
        if (membership.getBalanceAmount().compareTo(BigDecimal.ZERO) == 0) {
            membership.setStatus("ACTIVE");
        }
        membershipRepository.save(membership);

        PaymentResponse response = PaymentResponse.from(savedPayment);
        response.setPaymentStatus(membership.getPaymentStatus());
        return response;
    }

    public List<PaymentResponse> getPayments(String filter) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end = today;

        String normalized = filter == null ? "month" : filter.toLowerCase();
        switch (normalized) {
            case "today" -> start = today;
            case "week" -> start = today.with(DayOfWeek.MONDAY);
            case "month" -> start = today.withDayOfMonth(1);
            case "year" -> start = today.withDayOfYear(1);
            default -> throw new BadRequestException("Unsupported payment filter: " + filter);
        }

        return paymentRepository.findByPaymentDateBetweenOrderByPaymentDateDescIdDesc(start, end)
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
