package com.payment.kientv84.dtos.responses.kafka;

import com.payment.kientv84.dtos.responses.PaymentMethodResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaPaymentResponse {
    private UUID id;
    private UUID orderId;
    private UUID userId;
    private String orderCode;
    private PaymentMethodResponse paymentMethod;
    private BigDecimal amount;
    private String status;
    private String transactionCode;
    private Date createdDate;
    private Date updateDate;
}

