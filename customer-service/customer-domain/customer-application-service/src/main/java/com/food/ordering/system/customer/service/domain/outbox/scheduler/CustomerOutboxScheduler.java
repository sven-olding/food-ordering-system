package com.food.ordering.system.customer.service.domain.outbox.scheduler;

import com.food.ordering.system.customer.service.domain.outbox.CustomerOutboxHelper;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerCreatedEventMessagePublisher;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerOutboxScheduler implements OutboxScheduler {
    private final CustomerOutboxHelper customerOutboxHelper;
    private final CustomerCreatedEventMessagePublisher customerCreatedEventMessagePublisher;

    @Override
    @Transactional
    @Scheduled(fixedRateString = "${customer-service.outbox-scheduler-fixed-rate}",
            initialDelayString = "${customer-service.outbox-scheduler-initial-delay}")
    public void processOutboxMessages() {
        Optional<List<CustomerOutboxMessage>> customerOutboxMessages = customerOutboxHelper
                .getCustomerOutboxMessageByOutboxStatus(OutboxStatus.STARTED);
        if(customerOutboxMessages.isEmpty())
            return;

        List<CustomerOutboxMessage> outboxMessages = customerOutboxMessages.get();
        log.info("Received {} CustomerOutboxMessage with ids {}, sending to message bus!", outboxMessages.size(),
                outboxMessages.stream().map(outboxMessage ->
                        outboxMessage.getId().toString()).collect(Collectors.joining(",")));

        outboxMessages.forEach(customerOutboxMessage ->
                customerCreatedEventMessagePublisher.publish(customerOutboxMessage,
                        customerOutboxHelper::updateOutboxMessage));
    }
}
