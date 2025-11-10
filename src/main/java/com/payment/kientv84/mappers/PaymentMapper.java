package com.payment.kientv84.mappers;

import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import com.payment.kientv84.entities.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "updatedDate", target = "updateDate")
    @Mapping(source = "status", target = "status")
    PaymentResponse mapToPaymentResponse(PaymentEntity payment);

    KafkaPaymentResponse mapToKafkaPaymentResponse(PaymentResponse paymentResponse);
}
