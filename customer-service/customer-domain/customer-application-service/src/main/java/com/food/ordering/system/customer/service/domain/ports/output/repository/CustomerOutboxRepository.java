package com.food.ordering.system.customer.service.domain.ports.output.repository;

import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.outbox.OutboxStatus;

import java.util.List;
import java.util.Optional;

public interface CustomerOutboxRepository {
    CustomerOutboxMessage save(CustomerOutboxMessage customerOutboxMessage);

    Optional<List<CustomerOutboxMessage>> findByOutboxStatus(OutboxStatus status);

    void deleteByOutboxStatus(OutboxStatus status);
}
