package com.payment.kientv84.messagsing.producer;

import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.properties.KafkaTopicProperties;
import com.payment.kientv84.services.KafkaService;
import com.payment.kientv84.ultis.KafkaObjectError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducerImpl implements PaymentProducer{
    private final KafkaTopicProperties kafkaTopicProperties;
    private final KafkaService kafkaService;

    @Override
    public void producePaymentEventSuccess(PaymentResponse message) {
        var topic = kafkaTopicProperties.getPaymentChecked();
        log.info("[produceOrderEventSuccess] producing order to topic {}", topic);
        kafkaService.send(topic, message);
    }

    @Override
    public void produceMessageError(KafkaObjectError kafkaObject) {
        var topic = kafkaTopicProperties.getErrorPayment();
        log.info("[produceMessageError] producing error to topic {}", topic);
        kafkaService.send(topic, kafkaObject);
    }
}
