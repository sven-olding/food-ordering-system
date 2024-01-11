package com.food.ordering.system.customer.service.domain.mapper;

import com.food.ordering.system.customer.service.domain.CustomerCreateHelper;
import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerResponse;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerCreatedEventPayload;
import com.food.ordering.system.order.service.domain.entity.Customer;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;

import static com.food.ordering.system.domain.DomainConstants.TIMEZONE_ID;

@Component
public class CustomerDataMapper {
    public Customer createCustomerCommandToCustomer(CreateCustomerCommand createCustomerCommand) {
        return Customer.builder()
                .username(createCustomerCommand.username())
                .firstName(createCustomerCommand.firstName())
                .lastName(createCustomerCommand.lastName())
                .build();
    }

    public CreateCustomerResponse customerCreatedEventToCreateCustomerResponse(
            CustomerCreatedEvent customerCreatedEvent) {
        return CreateCustomerResponse.builder()
                .customerId(customerCreatedEvent.getCustomer().getId().getValue())
                .createdAt(customerCreatedEvent.getCreatedAt())
                .message("Customer created successfully")
                .build();
    }

    public CustomerCreatedEventPayload customerCreatedEventToCustomerCreatedEventPayload(
            CustomerCreatedEvent customerCreatedEvent) {
        return CustomerCreatedEventPayload.builder()
                .firstName(customerCreatedEvent.getCustomer().getFirstName())
                .username(customerCreatedEvent.getCustomer().getUsername())
                .lastName(customerCreatedEvent.getCustomer().getLastName())
                .customerId(customerCreatedEvent.getCustomer().getId().getValue().toString())
                .createdAt(customerCreatedEvent.getCreatedAt())
                .build();
    }

    public GetCustomerResponse customerToGetCustomerResponse(Customer customer) {
        return new GetCustomerResponse(
                customer.getId().getValue().toString(),
                customer.getUsername(),
                customer.getFirstName(),
                customer.getLastName(),
                Collections.emptyList());
    }
}
