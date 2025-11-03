package com.payment.kientv84.controller;

import com.payment.kientv84.dtos.responses.PaymentMethodResponse;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentResponse>> getAllPayment() {
        return ResponseEntity.ok(paymentService.getAllPayment());
    }

    @GetMapping("/payment/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }
}

