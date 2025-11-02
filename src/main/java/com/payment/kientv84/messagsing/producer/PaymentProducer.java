package com.payment.kientv84.messagsing.producer;

import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.ultis.KafkaObjectError;

public interface PaymentProducer {
    void producePaymentEventSuccess(PaymentResponse message);

    void produceMessageError(KafkaObjectError kafkaObject);
}
