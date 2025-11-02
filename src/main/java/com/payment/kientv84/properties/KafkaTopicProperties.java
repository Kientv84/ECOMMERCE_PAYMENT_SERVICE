package com.payment.kientv84.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties("spring.kafka.payment.topic")
public class KafkaTopicProperties {
    private String errorPayment;
    private String paymentChecked;
}