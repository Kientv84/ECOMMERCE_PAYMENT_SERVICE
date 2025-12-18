package com.payment.kientv84.messaging.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.kientv84.dtos.responses.kafka.KafkaEvent;
import com.payment.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.payment.kientv84.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.descriptor.java.ObjectJavaType;
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
    public void onMessageHandler(@Payload String message) {
        try {
            log.info("[onMessageHandler] Start consuming message ...");
            log.info("[onMessageHandler] Received message payload: {}", message);

//            KafkaOrderResponse response = new ObjectMapper().readValue(message, KafkaOrderResponse.class);

            ObjectMapper objectMapper = new ObjectMapper();

            KafkaEvent<KafkaOrderResponse> event =
                    objectMapper.readValue(
                            message,
                            new TypeReference<KafkaEvent<KafkaOrderResponse>>() {}
                    );

            KafkaOrderResponse payload = event.getPayload();

            paymentService.processPayment(payload);
            log.info("[onMessageHandler] Process payment success ...");
        } catch (Exception e) {
            log.error("[onMessageHandler] Error while Process payment . Err {}", e.getMessage());
        }
    }
}
