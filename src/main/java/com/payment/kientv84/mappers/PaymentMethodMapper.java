package com.payment.kientv84.mappers;

import com.payment.kientv84.dtos.responses.PaymentMethodResponse;
import com.payment.kientv84.entities.PaymentMethodEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {
    PaymentMethodResponse mapToPaymentMethodResponse(PaymentMethodEntity paymentMethodEntity);
}
