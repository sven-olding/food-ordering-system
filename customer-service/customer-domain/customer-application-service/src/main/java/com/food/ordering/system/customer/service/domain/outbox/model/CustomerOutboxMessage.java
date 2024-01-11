package com.food.ordering.system.customer.service.domain.outbox.model;

import com.food.ordering.system.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CustomerOutboxMessage {
    private UUID id;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String payload;
    @Setter
    private OutboxStatus outboxStatus;
    private int version;
}
