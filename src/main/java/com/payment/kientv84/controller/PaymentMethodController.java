package com.payment.kientv84.controller;

import com.payment.kientv84.dtos.requests.PaymentMethodRequest;
import com.payment.kientv84.dtos.requests.PaymentMethodUpdateRequest;
import com.payment.kientv84.dtos.responses.PaymentMethodResponse;
import com.payment.kientv84.services.PaymentMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api")
public class PaymentMethodController {
    private final PaymentMethodService paymentMethodService;

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethodResponse>> getAllPaymentMethod() {
        return ResponseEntity.ok(paymentMethodService.getAllPaymentMethod());
    }

    @PostMapping("/payment-method")
    public ResponseEntity<PaymentMethodResponse> createPaymentMethod(@Valid @RequestBody PaymentMethodRequest paymentMethodRequest) {
        return ResponseEntity.ok(paymentMethodService.createPaymentMethod(paymentMethodRequest));
    }

    @GetMapping("/payment-method/{id}")
    public ResponseEntity<PaymentMethodResponse> getPaymentMethodById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentMethodService.getPaymentMethodById(id));
    }

    @PostMapping("/payment-method/{id}")
    public ResponseEntity<PaymentMethodResponse> updatePaymentMethodById(@PathVariable UUID id, @RequestBody PaymentMethodUpdateRequest updateData) {
        return ResponseEntity.ok(paymentMethodService.updatePaymentMethodById(id, updateData));
    }

    @PostMapping("/payment-methods")
    public String deletePaymentMethod(@RequestBody List<UUID> uuids) {
        return paymentMethodService.deletePaymentMethod(uuids);
    }
}
