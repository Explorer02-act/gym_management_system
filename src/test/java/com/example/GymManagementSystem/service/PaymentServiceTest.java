package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.PaymentRequest;
import com.example.GymManagementSystem.dto.PaymentResponse;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.model.Payment;
import com.example.GymManagementSystem.repository.MembershipRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentServiceTest {

    @Test
    void addPaymentShouldUpdateMembershipBalanceAndStatus() {
        MembershipRepository membershipRepository = mock(MembershipRepository.class);
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        PaymentService paymentService = new PaymentService(paymentRepository, membershipRepository);

        Member member = new Member();
        member.setId(1L);
        member.setName("John Doe");
        member.setMemberCode("M001");

        Membership membership = new Membership();
        membership.setId(5L);
        membership.setPlanType("6 MONTH");
        membership.setPlanPrice(new BigDecimal("10000"));
        membership.setTotalAmount(new BigDecimal("10000"));
        membership.setAmountPaid(new BigDecimal("6000"));
        membership.setBalanceAmount(new BigDecimal("4000"));
        membership.setPaymentStatus("PARTIAL");
        membership.setMember(member);
        membership.setStatus("ACTIVE");

        when(membershipRepository.findById(5L)).thenReturn(Optional.of(membership));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentRequest request = new PaymentRequest();
        request.setMembershipId(5L);
        request.setAmount(new BigDecimal("4000"));
        request.setPaymentMode("GPAY");
        request.setTransactionId("TXN123");
        request.setRemarks("Settlement");

        PaymentResponse response = paymentService.addPayment(request);

        assertThat(response.getAmount()).isEqualByComparingTo("4000");
        assertThat(response.getPaymentStatus()).isEqualTo("PAID");
        assertThat(membership.getAmountPaid()).isEqualByComparingTo("10000");
        assertThat(membership.getBalanceAmount()).isEqualByComparingTo("0");
        assertThat(membership.getPaymentStatus()).isEqualTo("PAID");
        verify(paymentRepository).save(any(Payment.class));
    }
}
