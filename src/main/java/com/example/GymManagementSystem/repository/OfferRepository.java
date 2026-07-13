package com.example.GymManagementSystem.repository;

import com.example.GymManagementSystem.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByActiveTrueOrderByDiscountPercentageDesc();

    List<Offer> findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate startDate, LocalDate endDate);
}
