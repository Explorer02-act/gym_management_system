package com.example.GymManagementSystem.service;

import com.example.GymManagementSystem.dto.OfferRequest;
import com.example.GymManagementSystem.dto.OfferResponse;
import com.example.GymManagementSystem.exception.BadRequestException;
import com.example.GymManagementSystem.exception.ResourceNotFoundException;
import com.example.GymManagementSystem.model.MembershipPlan;
import com.example.GymManagementSystem.model.Offer;
import com.example.GymManagementSystem.repository.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class OfferService {

    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<OfferResponse> getAllOffers() {
        seedDefaultOffersIfEmpty();
        return offerRepository.findAll().stream().map(OfferResponse::from).toList();
    }

    public List<OfferResponse> getActiveOffers() {
        seedDefaultOffersIfEmpty();
        return offerRepository.findByActiveTrueOrderByDiscountPercentageDesc().stream().map(OfferResponse::from).toList();
    }

    @Transactional
    public OfferResponse createOffer(OfferRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());

        Offer offer = new Offer();
        apply(offer, request);
        return OfferResponse.from(offerRepository.save(offer));
    }

    @Transactional
    public OfferResponse updateOffer(Long id, OfferRequest request) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + id));
        validateDates(request.getStartDate(), request.getEndDate());
        apply(offer, request);
        return OfferResponse.from(offerRepository.save(offer));
    }

    @Transactional
    public void deleteOffer(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + id));
        offer.setActive(false);
        offerRepository.save(offer);
    }

    public BigDecimal calculateFinalPrice(MembershipPlan plan) {
        if (plan == null || plan.getDisplayPrice() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal basePrice = plan.getDisplayPrice();
        return getBestActiveOffer().map(offer -> applyDiscount(basePrice, offer.getDiscountPercentage()))
                .orElse(basePrice);
    }

    public Optional<Offer> getBestActiveOffer() {
        seedDefaultOffersIfEmpty();
        return offerRepository.findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate.now(), LocalDate.now())
                .stream()
                .max(Comparator.comparing(Offer::getDiscountPercentage));
    }

    private void apply(Offer offer, OfferRequest request) {
        offer.setOfferName(request.getOfferName());
        offer.setDiscountPercentage(request.getDiscountPercentage());
        offer.setStartDate(request.getStartDate());
        offer.setEndDate(request.getEndDate());
        if (request.getActive() != null) {
            offer.setActive(request.getActive());
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date cannot be before start date");
        }
    }

    private BigDecimal applyDiscount(BigDecimal basePrice, BigDecimal discountPercentage) {
        BigDecimal percent = discountPercentage.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        BigDecimal discountedPrice = basePrice.multiply(BigDecimal.ONE.subtract(percent));
        return discountedPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private void seedDefaultOffersIfEmpty() {
        if (offerRepository.count() > 0) {
            return;
        }

        LocalDate today = LocalDate.now();
        Offer offer = new Offer();
        offer.setOfferName("Diwali Offer");
        offer.setDiscountPercentage(new BigDecimal("10"));
        offer.setStartDate(today.minusDays(7));
        offer.setEndDate(today.plusDays(30));
        offer.setActive(true);
        offerRepository.save(offer);
    }
}
