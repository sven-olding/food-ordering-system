package com.food.ordering.system.customer.service.dataaccess.outbox.mapper;

import com.food.ordering.system.customer.service.dataaccess.outbox.entity.CustomerOutboxEntity;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerOutboxDataAccessMapper {
    public CustomerOutboxMessage customerOutboxEntityToCustomerOutboxMessage(CustomerOutboxEntity customerOutboxEntity) {
        return CustomerOutboxMessage.builder()
                .id(customerOutboxEntity.getId())
                .createdAt(customerOutboxEntity.getCreatedAt())
                .payload(customerOutboxEntity.getPayload())
                .outboxStatus(customerOutboxEntity.getOutboxStatus())
                .version(customerOutboxEntity.getVersion())
                .build();
    }

    public CustomerOutboxEntity customerOutboxMessageToOutboxEntity(
            CustomerOutboxMessage customerOutboxMessage) {
        return CustomerOutboxEntity.builder()
                .id(customerOutboxMessage.getId())
                .createdAt(customerOutboxMessage.getCreatedAt())
                .payload(customerOutboxMessage.getPayload())
                .outboxStatus(customerOutboxMessage.getOutboxStatus())
                .version(customerOutboxMessage.getVersion())
                .build();
    }

}
