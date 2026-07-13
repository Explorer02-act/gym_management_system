package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.EnrollmentResponse;
import com.example.GymManagementSystem.dto.RenewalPaymentRequest;
import com.example.GymManagementSystem.service.RenewalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/renewals")
public class RenewalController {

    private final RenewalService renewalService;

    public RenewalController(RenewalService renewalService) {
        this.renewalService = renewalService;
    }

    @PostMapping("/member/{memberId}")
    public EnrollmentResponse renew(@PathVariable Long memberId,
                                    @Valid @RequestBody RenewalPaymentRequest request) {
        return renewalService.renew(memberId, request);
    }
}
