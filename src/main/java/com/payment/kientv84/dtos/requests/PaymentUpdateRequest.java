package com.payment.kientv84.dtos.requests;

import com.payment.kientv84.commons.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PaymentUpdateRequest {
    private String status;
}
