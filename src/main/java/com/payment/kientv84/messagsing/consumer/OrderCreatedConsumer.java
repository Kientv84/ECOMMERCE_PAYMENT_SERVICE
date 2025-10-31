package com.payment.kientv84.messagsing.consumer;

import com.payment.kientv84.dtos.responses.KafkaOrderResponse;
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
            topics = "${spring.kafka.order.topic.created-order}",
            groupId = "spring.kafka.order.group")
    public void onMessageHandler(@Payload KafkaOrderResponse message) {
        try {
            log.info("[onMessageHandler] Start consuming message ...");
            paymentService.processPayment(message);
            log.info("[onMessageHandler] Created account ...");
        } catch (Exception e) {
            log.error("[onMessageHandler] Error while creating account . Err {}", e.getMessage());
        }
    }
}
