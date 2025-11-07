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
public class MomoProcessor implements PaymentProcessor {
    private final PaymentMethodMapper paymentMethodMapper;

    @Override
    public PaymentResponse process(PaymentEntity payment) {
        if ( payment == null ) {
            throw new ServiceException(EnumError.PAYMENT_PROCESS_NOTNULL, "payment.process.notnull");
        }

        payment.setStatus(PaymentStatus.PENDING);
        payment.setNote("MOMO - wait for authentica");

        //  Giả lập MoMo thanh toán thành công ngay lập tức
        payment.setStatus(PaymentStatus.PAID);
        payment.setNote("MOMO - payment simulated as successful");

        // Tạo transactionCode nếu chưa có
        if (payment.getTransactionCode() == null) {
            payment.setTransactionCode("MOMO-" + UUID.randomUUID());
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .status(payment.getStatus().name())
                .transactionCode(payment.getTransactionCode())
                .orderCode(payment.getOrderCode())
                .paymentMethod(
                        paymentMethodMapper.mapToPaymentMethodResponse(payment.getPaymentMethod())
                )
                .amount(payment.getAmount())
                .build();
    }
}
