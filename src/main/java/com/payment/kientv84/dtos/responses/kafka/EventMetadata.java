package com.payment.kientv84.dtos.responses.kafka;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventMetadata {
    private UUID eventId;
    private String eventType;
    private String source;
    private int version;
}

