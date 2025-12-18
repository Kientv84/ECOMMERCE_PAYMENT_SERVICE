package com.payment.kientv84.messaging.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.kientv84.dtos.responses.kafka.KafkaEvent;
import com.payment.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.payment.kientv84.dtos.responses.kafka.KafkaPaymentUpdated;
import com.payment.kientv84.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderUpdatedPayment {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${spring.kafka.order.topic.order-event-payment-updated}",
            groupId = "spring.kafka.order.group",
            containerFactory = "kafkaListenerContainerFactory")
    public void onMessageHandler(@Payload String message) {
        try {
            log.info("[onMessageHandler] Start consuming message ...");
            log.info("[onMessageHandler] Received message payload: {}", message);

            ObjectMapper objectMapper = new ObjectMapper();

            KafkaEvent<KafkaPaymentUpdated> event =
                    objectMapper.readValue(
                            message,
                            new TypeReference<KafkaEvent<KafkaPaymentUpdated>>() {}
                    );

            KafkaPaymentUpdated payload = event.getPayload();

            paymentService.updateStatusFromOrderDelivered(payload);
            log.info("[onMessageHandler] Process payment success ...");
        } catch (Exception e) {
            log.error("[onMessageHandler] Error while Process payment . Err {}", e.getMessage());
        }
    }
}
