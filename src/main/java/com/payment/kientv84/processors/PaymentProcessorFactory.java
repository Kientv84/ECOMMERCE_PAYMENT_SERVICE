package com.payment.kientv84.processors;

import com.payment.kientv84.exceptions.EnumError;
import com.payment.kientv84.exceptions.ServiceException;
import com.payment.kientv84.processors.paymentProcessorImpl.CodProcessor;
import com.payment.kientv84.processors.paymentProcessorImpl.MomoProcessor;
import com.payment.kientv84.processors.paymentProcessorImpl.PaypalProcessor;
import com.payment.kientv84.processors.paymentProcessorImpl.QrProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentProcessorFactory {
    private final CodProcessor codProcessor;
    private final MomoProcessor momoProcessor;
    private final PaypalProcessor paypalProcessor;
    private final QrProcessor qrProcessor;

    public PaymentProcessor getProcessor(String method) {
        return switch (method.toUpperCase()) {
            case "COD" -> codProcessor;
            case "PAYPAL" -> paypalProcessor;
            case "QR" -> qrProcessor;
            case "MOMO" -> momoProcessor;
            default -> throw new ServiceException(EnumError.INVALID_PAYMENT_METHOD, "invalid.payment.method");
        };
    }

}
