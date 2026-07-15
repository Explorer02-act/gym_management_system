package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.OfferRequest;
import com.example.GymManagementSystem.dto.OfferResponse;
import com.example.GymManagementSystem.service.OfferService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public OfferResponse createOffer(@Valid @RequestBody OfferRequest request) {
        return offerService.createOffer(request);
    }

    @GetMapping
    public List<OfferResponse> getOffers() {
        return offerService.getAllOffers();
    }

    @GetMapping("/active")
    public List<OfferResponse> getActiveOffers() {
        return offerService.getActiveOffers();
    }

    @PutMapping("/{id}")
    public OfferResponse updateOffer(@PathVariable Long id, @Valid @RequestBody OfferRequest request) {
        return offerService.updateOffer(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return "Offer deleted successfully";
    }
}

