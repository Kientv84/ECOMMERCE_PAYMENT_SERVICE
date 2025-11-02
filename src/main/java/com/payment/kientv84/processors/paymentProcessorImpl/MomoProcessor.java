package com.payment.kientv84.processors.paymentProcessorImpl;

import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.entities.PaymentEntity;
import com.payment.kientv84.processors.PaymentProcessor;
import org.springframework.stereotype.Component;

@Component
public class MomoProcessor implements PaymentProcessor {
    @Override
    public PaymentResponse process(PaymentEntity payment) {
        return null;
    }
}
