package com.example.GymManagementSystem.dto;

import com.example.GymManagementSystem.model.Offer;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OfferResponse {

    private Long id;
    private String offerName;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;

    public static OfferResponse from(Offer offer) {
        OfferResponse response = new OfferResponse();
        response.setId(offer.getId());
        response.setOfferName(offer.getOfferName());
        response.setDiscountPercentage(offer.getDiscountPercentage());
        response.setStartDate(offer.getStartDate());
        response.setEndDate(offer.getEndDate());
        response.setActive(offer.isActive());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
