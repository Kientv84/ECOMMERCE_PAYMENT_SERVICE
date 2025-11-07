package com.payment.kientv84.controller;

import com.payment.kientv84.dtos.requests.PaymentUpdateRequest;
import com.payment.kientv84.dtos.responses.PaymentMethodResponse;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/payment/{id}")
    public ResponseEntity<PaymentResponse> updatePaymentById(@PathVariable UUID id, @RequestBody PaymentUpdateRequest updateRequest) {
        return ResponseEntity.ok(paymentService.updatePaymentStatus(id, updateRequest));
    }
}

