package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.model.GymSettings;
import com.example.GymManagementSystem.model.Member;
import com.example.GymManagementSystem.model.Membership;
import com.example.GymManagementSystem.model.Payment;
import com.example.GymManagementSystem.repository.GymSettingsRepository;
import com.example.GymManagementSystem.repository.PaymentRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReceiptServiceTest {

    @Test
    void generateReceiptShouldCreatePdfBytes() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        GymSettingsRepository gymSettingsRepository = mock(GymSettingsRepository.class);
        ReceiptService receiptService = new ReceiptService(paymentRepository, gymSettingsRepository);

        Member member = new Member();
        member.setName("Aarav Kumar");
        member.setMemberCode("MM005");

        Membership membership = new Membership();
        membership.setPlanType("Elite");
        membership.setBalanceAmount(BigDecimal.valueOf(500));

        Payment payment = new Payment();
        payment.setId(42L);
        payment.setMember(member);
        payment.setMembership(membership);
        payment.setAmount(BigDecimal.valueOf(2500));
        payment.setPaymentMode("GPAY");
        payment.setTransactionId("TXN-101");
        payment.setPaymentDate(LocalDate.of(2026, 7, 13));

        when(paymentRepository.findById(42L)).thenReturn(Optional.of(payment));

        GymSettings settings = new GymSettings();
        settings.setGymName("MUSCLE MONSTERZ");
        when(gymSettingsRepository.findAll()).thenReturn(List.of(settings));

        byte[] pdfBytes = receiptService.generateReceipt(42L);

        assertThat(pdfBytes).isNotEmpty();
        assertThat(new String(pdfBytes, StandardCharsets.ISO_8859_1)).contains("%PDF");
    }
}
