package com.payment.kientv84.dtos.requests;

import com.payment.kientv84.commons.PaymentMethodStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PaymentMethodUpdateRequest {
    private String code;
    private String name;
    private String description;
    private PaymentMethodStatus status;
}