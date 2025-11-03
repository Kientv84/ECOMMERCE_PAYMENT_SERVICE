package com.payment.kientv84.messaging.consumer;

import com.payment.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.payment.kientv84.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderCreatedConsumer {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "${spring.kafka.order.topic.order-created}",
            groupId = "spring.kafka.order.group",
            containerFactory = "kafkaListenerContainerFactory")
    public void onMessageHandler(@Payload KafkaOrderResponse message) {
        try {
            log.info("[onMessageHandler] Start consuming message ...");
            log.info("[onMessageHandler] Received message payload: {}", message);
            paymentService.processPayment(message);
            log.info("[onMessageHandler] Process payment success ...");
        } catch (Exception e) {
            log.error("[onMessageHandler] Error while Process payment . Err {}", e.getMessage());
        }
    }
}
