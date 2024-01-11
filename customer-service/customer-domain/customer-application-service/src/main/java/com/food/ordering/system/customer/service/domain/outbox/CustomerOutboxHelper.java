package com.food.ordering.system.customer.service.domain.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerCreatedEventPayload;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.TIMEZONE_ID;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerOutboxHelper {
    private final CustomerOutboxRepository customerOutboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void deleteCustomerOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
        customerOutboxRepository.deleteByOutboxStatus(outboxStatus);
    }

    @Transactional
    public void saveCustomerOutboxMessage(CustomerCreatedEventPayload customerCreatedEventPayload,
                                          OutboxStatus outboxStatus) {
        save(CustomerOutboxMessage.builder()
                .id(UUID.randomUUID())
                .createdAt(customerCreatedEventPayload.getCreatedAt())
                .processedAt(ZonedDateTime.now(ZoneId.of(TIMEZONE_ID)))
                .payload(createPayload(customerCreatedEventPayload))
                .outboxStatus(outboxStatus)
                .build());
    }

    @Transactional(readOnly = true)
    public Optional<List<CustomerOutboxMessage>> getCustomerOutboxMessageByOutboxStatus(OutboxStatus outboxStatus) {
        return customerOutboxRepository.findByOutboxStatus(outboxStatus);
    }

    @Transactional
    public void updateOutboxMessage(CustomerOutboxMessage customerOutboxMessage, OutboxStatus outboxStatus) {
        customerOutboxMessage.setOutboxStatus(outboxStatus);
        save(customerOutboxMessage);
        log.info("Customer outbox table status is updated as {}", outboxStatus.name());
    }

    private String createPayload(CustomerCreatedEventPayload customerCreatedEventPayload) {
        try {
            return objectMapper.writeValueAsString(customerCreatedEventPayload);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void save(CustomerOutboxMessage customerOutboxMessage) {
        CustomerOutboxMessage saved = customerOutboxRepository.save(customerOutboxMessage);
        if(saved == null) {
            log.error("Could not save CustomerOutboxMessage");
            throw new CustomerDomainException("Could not save CustomerOutboxMessage");
        }
        log.info("CustomerOutboxMessage saved with id: {}", saved.getId());
    }
}
