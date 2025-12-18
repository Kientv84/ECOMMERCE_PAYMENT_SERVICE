package com.payment.kientv84.messaging.producer;

import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.dtos.responses.kafka.EventMetadata;
import com.payment.kientv84.dtos.responses.kafka.KafkaEvent;
import com.payment.kientv84.dtos.responses.kafka.KafkaPaymentResponse;
import com.payment.kientv84.properties.KafkaTopicProperties;
import com.payment.kientv84.services.KafkaService;
import com.payment.kientv84.ultis.KafkaObjectError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProducerImpl implements PaymentProducer{
    private final KafkaTopicProperties kafkaTopicProperties;
    private final KafkaService kafkaService;

    @Override
    public void producePaymentEventCodePending(KafkaPaymentResponse kafkaPaymentResponse) {
        var topic = kafkaTopicProperties.getPaymentCodPending();
        log.info("[producePaymentEventCodePending] producing order to topic {}", topic);

        KafkaEvent<KafkaPaymentResponse> message = KafkaEvent.<KafkaPaymentResponse>builder()
                .metadata(EventMetadata.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(topic)
                        .source("payment-service")
                        .version(1)
                        .build())
                .payload(kafkaPaymentResponse)
                .build();
        kafkaService.send(topic, message);
    }

    @Override
    public void producePaymentEventFailed(KafkaPaymentResponse kafkaPaymentResponse) {
        var topic = kafkaTopicProperties.getPaymentFailed();
        log.info("[producePaymentEventFailed] producing order to topic {}", topic);

        KafkaEvent<KafkaPaymentResponse> message = KafkaEvent.<KafkaPaymentResponse>builder()
                .metadata(EventMetadata.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(topic)
                        .source("payment-service")
                        .version(1)
                        .build())
                .payload(kafkaPaymentResponse)
                .build();

        kafkaService.send(topic, message);
    }

    @Override
    public void producePaymentEventSuccess(KafkaPaymentResponse kafkaPaymentResponse) {
        var topic = kafkaTopicProperties.getPaymentSuccess();
        log.info("[producePaymentEventCodePending] producing order to topic {}", topic);

        KafkaEvent<KafkaPaymentResponse> message = KafkaEvent.<KafkaPaymentResponse>builder()
                .metadata(EventMetadata.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(topic)
                        .source("payment-service")
                        .version(1)
                        .build())
                .payload(kafkaPaymentResponse)
                .build();

        kafkaService.send(topic, message);
    }

    @Override
    public void produceMessageError(KafkaObjectError kafkaObject) {
        var topic = kafkaTopicProperties.getErrorPayment();
        log.info("[produceMessageError] producing error to topic {}", topic);
        kafkaService.send(topic, kafkaObject);
    }
}
