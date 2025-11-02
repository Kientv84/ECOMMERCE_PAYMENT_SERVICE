package com.payment.kientv84.processors;

import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.entities.PaymentEntity;

public interface PaymentProcessor {
    PaymentResponse process(PaymentEntity payment);
}
