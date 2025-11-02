package com.payment.kientv84.processors.paymentProcessorImpl;

import com.payment.kientv84.commons.PaymentStatus;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.entities.PaymentEntity;
import com.payment.kientv84.exceptions.EnumError;
import com.payment.kientv84.exceptions.ServiceException;
import com.payment.kientv84.mappers.PaymentMethodMapper;
import com.payment.kientv84.processors.PaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CodProcessor implements PaymentProcessor {
    private final PaymentMethodMapper paymentMethodMapper;

    @Override
    public PaymentResponse process(PaymentEntity payment) {
        if ( payment == null ) {
            throw new ServiceException(EnumError.PAYMENT_PROCESS_NOTNULL, "payment.process.notnull");
        }

        payment.setStatus(PaymentStatus.PENDING);
        payment.setNote("COD - wait for delivery confirmation");
        // có thể sinh transactionRef nội bộ nếu chưa có
        if (payment.getTransactionCode() == null) {
            payment.setTransactionCode("COD-" + UUID.randomUUID());
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .orderCode(payment.getOrderCode())
                .paymentMethod(paymentMethodMapper.mapToPaymentMethodResponse(payment.getPaymentMethod())) // tạm tạo mã thanh toán nội bộ
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .transactionRef(payment.getTransactionCode())
                .build();
    }
}
