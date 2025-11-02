package com.payment.kientv84.dtos.responses.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KafkaOrderResponse {
    private UUID id;
    private UUID userId;
    private String orderCode;
    private UUID paymentMethod;
    private String shippingAddress;
    private BigDecimal totalPrice;
}