package com.example.GymManagementSystem.controller;

import com.example.GymManagementSystem.dto.PaymentRequest;
import com.example.GymManagementSystem.dto.PaymentResponse;
import com.example.GymManagementSystem.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/add")
    public PaymentResponse addPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.addPayment(request);
    }

    @GetMapping
    public List<PaymentResponse> getPayments(@RequestParam(defaultValue = "month") String filter) {
        return paymentService.getPayments(filter);
    }
}
