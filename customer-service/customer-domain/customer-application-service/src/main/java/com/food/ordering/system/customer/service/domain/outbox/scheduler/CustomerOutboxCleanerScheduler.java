package com.food.ordering.system.customer.service.domain.outbox.scheduler;

import com.food.ordering.system.customer.service.domain.outbox.CustomerOutboxHelper;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerOutboxCleanerScheduler implements OutboxScheduler {
    private final CustomerOutboxHelper customerOutboxHelper;

    @Override
    @Transactional
    @Scheduled(cron = "@midnight")
    public void processOutboxMessages() {
        Optional<List<CustomerOutboxMessage>> customerOutboxMessages =
                customerOutboxHelper.getCustomerOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
        if(customerOutboxMessages.isEmpty() || customerOutboxMessages.get().isEmpty())
            return;

        log.info("Received {} CustomerOutboxMessage for clean-up!", customerOutboxMessages.get().size());
        customerOutboxHelper.deleteCustomerOutboxMessageByOutboxStatus(OutboxStatus.COMPLETED);
        log.info("Deleted {} CustomerOutboxMessage!", customerOutboxMessages.get().size());
    }
}
