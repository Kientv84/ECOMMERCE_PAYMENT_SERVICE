package com.payment.kientv84.controller;

import com.payment.kientv84.dtos.requests.PaymentUpdateRequest;
import com.payment.kientv84.dtos.requests.search.payment.PaymentSearchRequest;
import com.payment.kientv84.dtos.responses.PagedResponse;
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

    @PostMapping("/payments/filter")
    public ResponseEntity<PagedResponse<PaymentResponse>> getAllPayment(PaymentSearchRequest request) {
        return ResponseEntity.ok(paymentService.getAllPayment(request));
    }

    @GetMapping("/payments/suggestion")
    public ResponseEntity<List<PaymentResponse>> getPaymentSuggestions(@RequestParam String q,
                                                                   @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(paymentService.searchPaymentSuggestion(q, limit));
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

