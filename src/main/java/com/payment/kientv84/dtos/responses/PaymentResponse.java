package com.payment.kientv84.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private String orderCode;
    private PaymentMethodResponse paymentMethod;
    private BigDecimal amount;
    private String status;
    private String transactionRef;
    private Instant createdAt;
    private Instant updatedAt;
}

