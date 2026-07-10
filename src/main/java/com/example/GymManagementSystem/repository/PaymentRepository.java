package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByPaymentDateBetweenOrderByPaymentDateDescIdDesc(LocalDate startDate, LocalDate endDate);

    List<Payment> findByMemberIdOrderByPaymentDateDescIdDesc(Long memberId);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paymentDate = :date")
    BigDecimal sumByPaymentDate(@Param("date") LocalDate date);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.paymentDate between :startDate and :endDate")
    BigDecimal sumBetween(@Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);

    @Query("select coalesce(sum(p.amount), 0) from Payment p")
    BigDecimal sumLifetime();
}
