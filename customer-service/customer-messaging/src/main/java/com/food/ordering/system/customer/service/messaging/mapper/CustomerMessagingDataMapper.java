package com.food.ordering.system.customer.service.messaging.mapper;

import com.food.ordering.system.customer.service.domain.outbox.model.CustomerCreatedEventPayload;
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import org.springframework.stereotype.Component;

@Component
public class CustomerMessagingDataMapper {
    public CustomerAvroModel customerCreatedEventPayloadToCustomerAvroModel(
            CustomerCreatedEventPayload customerCreatedEventPayload) {
        return CustomerAvroModel.newBuilder()
                .setFirstName(customerCreatedEventPayload.getFirstName())
                .setUsername(customerCreatedEventPayload.getUsername())
                .setId(customerCreatedEventPayload.getCustomerId())
                .setLastName(customerCreatedEventPayload.getLastName())
                .build();
    }
}
