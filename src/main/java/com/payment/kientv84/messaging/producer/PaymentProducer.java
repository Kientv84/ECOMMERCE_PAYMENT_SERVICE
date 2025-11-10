package com.payment.kientv84.messaging.producer;

import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import com.payment.kientv84.ultis.KafkaObjectError;

public interface PaymentProducer {
    void producePaymentEventCodePending(KafkaPaymentResponse message);

    void producePaymentEventFailed(KafkaPaymentResponse message);

    void producePaymentEventSuccess(KafkaPaymentResponse message);

    void produceMessageError(KafkaObjectError kafkaObject);
}
