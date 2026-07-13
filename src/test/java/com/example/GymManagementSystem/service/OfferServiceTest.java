package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.model.MembershipPlan;
import com.example.GymManagementSystem.model.Offer;
import com.example.GymManagementSystem.repository.OfferRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferServiceTest {

    @Test
    void calculateFinalPriceShouldApplyHighestActiveDiscount() {
        OfferRepository offerRepository = mock(OfferRepository.class);
        OfferService offerService = new OfferService(offerRepository, mock(AuditLogService.class));

        MembershipPlan plan = new MembershipPlan();
        plan.setDisplayPrice(new BigDecimal("10000"));

        Offer offer = new Offer();
        offer.setOfferName("Diwali Offer");
        offer.setDiscountPercentage(new BigDecimal("10"));
        offer.setStartDate(LocalDate.now().minusDays(1));
        offer.setEndDate(LocalDate.now().plusDays(5));
        offer.setActive(true);

        when(offerRepository.findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate.now(), LocalDate.now()))
                .thenReturn(List.of(offer));

        BigDecimal finalPrice = offerService.calculateFinalPrice(plan);

        assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("9000"));
    }
}
