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

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final OfferRepository offerRepository;
    private final AuditLogService auditLogService;

    public OfferService(OfferRepository offerRepository, AuditLogService auditLogService) {
        this.offerRepository = offerRepository;
        this.auditLogService = auditLogService;
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
        validate(request);

        Offer offer = new Offer();
        apply(offer, request);
        Offer saved = offerRepository.save(offer);
        auditLogService.record("OFFER_CREATED:" + saved.getOfferName());
        return OfferResponse.from(saved);
    }

    @Transactional
    public OfferResponse updateOffer(Long id, OfferRequest request) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + id));
        validate(request);
        apply(offer, request);
        Offer saved = offerRepository.save(offer);
        auditLogService.record("OFFER_UPDATED:" + saved.getOfferName());
        return OfferResponse.from(saved);
    }

    @Transactional
    public void deleteOffer(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + id));
        offerRepository.delete(offer);
        auditLogService.record("OFFER_DELETED:" + offer.getOfferName());
    }

    public BigDecimal calculateFinalPrice(MembershipPlan plan) {
        if (plan == null || plan.getDisplayPrice() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal basePrice = plan.getDisplayPrice();
        return getBestActiveOffer(basePrice)
                .map(offer -> applyDiscount(basePrice, offer))
                .orElse(basePrice)
                .setScale(2, RoundingMode.HALF_UP);
    }

    public Optional<Offer> getBestActiveOffer() {
        return getBestActiveOffer(BigDecimal.ZERO);
    }

    private Optional<Offer> getBestActiveOffer(BigDecimal basePrice) {
        seedDefaultOffersIfEmpty();
        return offerRepository.findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate.now(), LocalDate.now())
                .stream()
                .max(Comparator.comparing(offer -> discountValue(basePrice, offer)));
    }

    private void apply(Offer offer, OfferRequest request) {
        offer.setOfferName(request.getOfferName());
        offer.setDiscountPercentage(defaultZero(request.getDiscountPercentage()));
        offer.setDiscountAmount(defaultZero(request.getDiscountAmount()));
        offer.setStartDate(request.getStartDate());
        offer.setEndDate(request.getEndDate());
        if (request.getActive() != null) {
            offer.setActive(request.getActive());
        }
    }

    private void validate(OfferRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());
        BigDecimal discountPercentage = defaultZero(request.getDiscountPercentage());
        BigDecimal discountAmount = defaultZero(request.getDiscountAmount());
        if (discountPercentage.compareTo(BigDecimal.ZERO) == 0 && discountAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("Enter a percentage discount or a cash discount amount");
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

    private BigDecimal applyDiscount(BigDecimal basePrice, Offer offer) {
        BigDecimal discountedPrice = basePrice.subtract(discountValue(basePrice, offer));
        if (discountedPrice.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return discountedPrice.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal discountValue(BigDecimal basePrice, Offer offer) {
        BigDecimal percent = defaultZero(offer.getDiscountPercentage()).divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
        BigDecimal percentageDiscount = basePrice.multiply(percent);
        return percentageDiscount.add(defaultZero(offer.getDiscountAmount())).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void seedDefaultOffersIfEmpty() {
        if (offerRepository.count() > 0) {
            return;
        }

        LocalDate today = LocalDate.now();
        Offer offer = new Offer();
        offer.setOfferName("Diwali Offer");
        offer.setDiscountPercentage(new BigDecimal("10"));
        offer.setDiscountAmount(BigDecimal.ZERO);
        offer.setStartDate(today.minusDays(7));
        offer.setEndDate(today.plusDays(30));
        offer.setActive(true);
        offerRepository.save(offer);
    }
}
